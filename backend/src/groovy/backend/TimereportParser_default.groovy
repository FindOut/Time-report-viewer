package backend

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.joda.time.DateTime

class TimereportParser_default {
    Workbook EXCEL_FILE = null
    Boolean EXCEL_FILE_OK = false
    Employee EMPLOYEE = null

    // The fiscal year this parser is valid for
    private List TIMEREPORT_PARSER_YEAR = [2014, 2015, 2016]
    // Index of first sheet with a date
    private int FIRST_REPORT_SHEET= 1
    // Location of the date cell for each report month
    private Map DATE_CELL = [row: 2,column: 1]
    // Location of month standard time
    private Map STANDARD_TIME_CELL = [row: 0, column: 10]
    // Location of the employeename for each report month
    private Map USER_NAME_CELL = [row: 1,column: 10]
    // Index of the column where activity name is found
    private int INDEX_OFFER_AREA_NAME = 0
    private int INDEX_ACTIVITY_NAME = 1
    private int INDEX_ACTIVITY_DATA_START = 3

    // String indicating when to stop iterating over activities
    private String END_OF_DATA_ROWS = 'Summa normaltid'

    // Housekeeping
    private FILENAME = ''
    private List NUMBER_TYPES = [0,2]
    private int FORMULA_TYPE = 2
    private int STRING_TYPE = 1
    private int MONTHS_IN_YEAR = 12

    private List<String> dividerHeaders = [
            'Debiterbar tid per EO',
            'Investerad tid i EO',
            'Annan FindOut tid',
            'Annan tid',
            'Summa normaltid'
    ]

    private Map<String, String> dividerMapping = [
            'Debiterbar tid per EO': 'Chargeable',
            'Investerad tid i EO': 'Investment',
            'Annan FindOut tid': 'Various FindOut time',
            'Annan tid': 'Various time',
    ]

    private List<String> defaultOfferAreaNamesForDividers = [
            'Not Specified',
            'Not Specified',
            'Other FindOut time',
            'Other time'
    ]

    // For performance
    private activities = Activity.list()
    private activityTypes = ActivityType.list()
    private offerAreas = OfferArea.list()
    private Range currentActivityDataRange = null
    private DateTime currentSheetDate = null
    private Sheet currentSheet = null
    private MonthlyReport currentMonthlyReport = null
    private ActivityType currentActivityType = null


    // Parser
    TimereportParser_default(File file){
        if(file){
            EXCEL_FILE = WorkbookFactory.create(file)
            FILENAME = file.getName()
            setEmployee()
            verifyTimereport()
        } else {
            println 'No file to parse'
        }
    }

    TimereportParser_default(InputStream stream, String fileName){
        try {
            EXCEL_FILE = WorkbookFactory.create(stream)
            FILENAME = fileName
            setEmployee()
            verifyTimereport()
        } catch (e) {
            println 'broken file: ' + fileName
            println e
        }
    }

    void parseWorkbook(){
        if(EXCEL_FILE_OK){
            createTimeReportMonths()

            (1..MONTHS_IN_YEAR).each{ int sheetIndex ->
                currentSheet = EXCEL_FILE.getSheetAt(sheetIndex)
                currentSheetDate = new DateTime(getCell(currentSheet, DATE_CELL).dateCellValue)
                currentActivityDataRange = getActivityDataRange(INDEX_ACTIVITY_DATA_START, currentSheetDate.dayOfMonth().getMaximumValue())
                currentMonthlyReport = createEmployeeStandardMonth()
                currentMonthlyReport.save(flush: true)

                parseActivityGroups()
            }
        }
    }

    private MonthlyReport createEmployeeStandardMonth(){
        double employeeMonthStandardTime = getCell(currentSheet, STANDARD_TIME_CELL).numericCellValue.round(2)

        MonthlyReport.findOrSaveByEmployeeAndStandardTimeAndDate(EMPLOYEE, employeeMonthStandardTime, currentSheetDate.toDate())
    }

    private void parseActivityGroups(){
        List dividerIndexes = getCategoryDividersForSheet()
        int lastDividerIndex = dividerIndexes.size()-1

        dividerIndexes.eachWithIndex{ dividerRowNumber, dividerIndex ->
            currentActivityType = null
            if(dividerIndex != lastDividerIndex){
                int start = dividerRowNumber as int
                int end = dividerIndexes[dividerIndex+1] as int

                currentActivityType = getCurrentActivityType(dividerIndex)

                ((start+1)..(end-1)).each{ int rowNumber ->
                    parseRow(currentSheet.getRow(rowNumber), dividerIndex)
                }
            }
        }
    }

    private getCurrentActivityType(dividerIndex){
        findOrSaveActivityTypeByName(dividerMapping[dividerHeaders[dividerIndex]])
    }

    private List getCategoryDividersForSheet(){
        currentSheet.rowIterator().findIndexValues { Row row ->
            getStringValue(row.getCell(1))?.trim() in dividerHeaders
        }
    }

    private void parseRow(Row row, int dividerIndex){
        String activityName = getStringValue(row.getCell(INDEX_ACTIVITY_NAME))
        String offerAreaName = getStringValue(row.getCell(INDEX_OFFER_AREA_NAME)) ?: defaultOfferAreaNamesForDividers[dividerIndex]

        if(activityName){
            currentActivityDataRange.eachWithIndex{ int columnNumber, index ->
                Date activityReportDate = currentSheetDate.plusDays(index).toDate()

                // Get activity hours from cell
                double hours = getActivityHour(row.getCell(columnNumber))

                if(hours > 0){
                    // Now that we have data, create an activity and offer area if we didn't have one
                    OfferArea offerArea = findOrSaveOfferAreaByName(offerAreaName)
                    Activity activity = findOrSaveActivity(activityName, offerArea)

                    createAndSaveWorkday(activity, activityReportDate, hours)
                }
            }
        }
    }

    private verifyTimereport(){
        int fileYear = getTimereportYear()
        Boolean excelFileOk = true

        // Is the correct parser used?
        if(!(fileYear in TIMEREPORT_PARSER_YEAR)){
            excelFileOk = false
            println "Wrong parser used. Tried to parse $fileYear file with $TIMEREPORT_PARSER_YEAR parser"
        }

        // Does the file contain a employee name?
        if(!EMPLOYEE){
            excelFileOk = false
            println "No employeename found in file: '$FILENAME'"
        }

        EXCEL_FILE_OK = excelFileOk
    }

    private createTimeReportMonths(){
        Sheet myDashboard = EXCEL_FILE.getSheetAt(0)
        DateTime FirstSheetDate = new DateTime(getCell(EXCEL_FILE.getSheetAt(FIRST_REPORT_SHEET), DATE_CELL).dateCellValue)

        MONTHS_IN_YEAR.times { timeReportMonthIndex ->
            Cell monthStandardTimeCell = getCell(myDashboard, [row: 5, column: (1+timeReportMonthIndex)])

            if(monthStandardTimeCell.cellType == FORMULA_TYPE){
                // values like F5*176-13*4 screws this up
                List formulaParts = monthStandardTimeCell.cellFormula.split('\\*')

                try {
                    int standardTime = formulaParts[1].toInteger()

                    TimeReportMonth.findOrSaveByDateAndStandardTime(FirstSheetDate.plusMonths(timeReportMonthIndex).toDate(), standardTime)
                } catch(e) {
                    println "Bad standard time formula for user $employeeName at monthindex $timeReportMonthIndex. Got parts $formulaParts"
                }

            }
        }
    }

    private double getActivityHour(Cell cell){
        // Get hours value and round to 2 decimals
        (cell && cell.getCellType() in NUMBER_TYPES ) ? cell.numericCellValue.round(2) : 0
    }

    private int getTimereportYear(){
        Sheet sheet = EXCEL_FILE.getSheetAt(FIRST_REPORT_SHEET)
        return new DateTime(getCell(sheet, DATE_CELL).dateCellValue).getYear()
    }

    private String getStringValue(Cell cell){
        // Trim those pesky whitespaces!
        (cell?.getCellType() == STRING_TYPE) ? cell.getStringCellValue().trim() : null
    }

    private OfferArea findOrSaveOfferAreaByName(String offerAreaName){
        OfferArea offerArea = offerAreas.find{it.name.toLowerCase() == offerAreaName.toLowerCase()}
        if(!offerArea){
            offerArea = new OfferArea(name: offerAreaName)
            offerArea.save()
            offerAreas << offerArea
        }

        offerArea
    }

    private findOrSaveActivity(activityName, offerArea){
        Activity activity = activities.find{it.name.toLowerCase() == activityName.toLowerCase() && it.offerArea.id == offerArea.id}
        if(!activity){
            activity = new Activity(
                    name: activityName,
                    activityType: currentActivityType,
                    offerArea: offerArea
            )
            activity.save()
            activities << activity
        }

        activity
    }

    private ActivityType findOrSaveActivityTypeByName(String activityTypeName){
        ActivityType activityType = activityTypes.find{it.name == activityTypeName}
        if(!activityType){
            activityType = new ActivityType(name: activityTypeName)
            activityType.save()
            activityTypes << activityType
        }

        activityType
    }

    private void createAndSaveWorkday(activity, date, hours){
        ActivityReport activityReport = ActivityReport.findOrCreateWhere(
                employee: EMPLOYEE,
                date: date,
                activity: activity
        )

        activityReport.hours = hours

        currentMonthlyReport.addToActivityReports(activityReport)
    }

    private static Range getActivityDataRange(indexStart, daysInMonth){
        (indexStart..indexStart+daysInMonth-1)
    }

    private void setEmployee(){
        String employeeName = getEmployeeName()

        EMPLOYEE = employeeName ? Employee.findOrSaveWhere(name: employeeName) : null
    }

    private String getEmployeeName(){
        String name
        // Get name from 'Namn' fields in sheet
        (1..MONTHS_IN_YEAR).each{ int sheetIndex ->
            Sheet sheet = EXCEL_FILE.getSheetAt(sheetIndex)
            Cell nameCell = getCell(sheet, USER_NAME_CELL)
            if(nameCell.cellType == STRING_TYPE){
                name = name?: getCell(sheet, USER_NAME_CELL).stringCellValue
            }
        }

        // If no name in sheets, get name from filename
        if (!name){
            name = FILENAME.split(' - ')[0]
        }

        name.trim()
    }
    private static Cell getCell(Sheet sheet, map ){
        sheet.getRow(map.row).getCell(map.column)
    }
}
