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
    User USER = null

    // The fiscal year this parser is valid for
    private List TIMEREPORT_PARSER_YEAR = [2014, 2015]
    // Index of first sheet with a date
    private int FIRST_REPORT_SHEET= 1
    // Location of the date cell for each report month
    private Map DATE_CELL = [row: 2,column: 1]
    // Location of month standard time
    private Map STANDARD_TIME_CELL = [row: 0, column: 10]
    // Location of the username for each report month
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
    private int STRING_TYPE = 1
    private int MONTHS_IN_YEAR = 12

    // For performance
    private offerAreas = OfferArea.list()
    private DateTime sheetDate = null
    private Sheet currentSheet = null


    TimereportParser_default(File file){
        if(file){
            EXCEL_FILE = WorkbookFactory.create(file)
            FILENAME = file.getName()
            setUser()
            verifyTimereport()
        } else {
            println 'No file to parse'
        }
    }

    TimereportParser_default(InputStream stream, String fileName){
        try {
            EXCEL_FILE = WorkbookFactory.create(stream)
            FILENAME = fileName
            setUser()
            verifyTimereport()
        } catch (e) {
            println 'broken file: ' + fileName
            println e
        }
    }

    List dividerHeaders = [
            'Debiterbar tid per EO',
            'Investerad tid i EO',
            'Annan FindOut tid',
            'Annan tid',
            'Summa normaltid'
    ]

    List getCategoryDividersForSheet(Sheet sheet){
        sheet.rowIterator().findIndexValues { Row row ->
            getStringValue(row.getCell(1))?.trim() in dividerHeaders
        }
    }

    void parseWorkbook(){
        if(EXCEL_FILE_OK){
            createTimeReportMonths()

            (1..MONTHS_IN_YEAR).each{ int sheetIndex ->
                currentSheet = EXCEL_FILE.getSheetAt(sheetIndex)

                // set date on sheet
                sheetDate = new DateTime(getCell(currentSheet, DATE_CELL).dateCellValue)
                double monthStandardTime = getCell(currentSheet, [row: 0, column: 10]).numericCellValue.round(2)

                UserTimeReportMonth.findOrSaveByUserAndStandardTimeAndTimeReportMonth(USER, monthStandardTime, sheetDate.toDate())

                // Get activities
                parseActivityGroups()
            }
        }
    }

    private void parseActivityGroups(){
        List dividerIndexes = getCategoryDividersForSheet(currentSheet)
        int lastDividerIndex = dividerIndexes.size()-1

        dividerIndexes.eachWithIndex{ dividerRowNumber, dividerIndex ->
            if(dividerIndex != lastDividerIndex){
                int start = dividerRowNumber as int
                int end = dividerIndexes[dividerIndex+1] as int

                ((start+1)..(end-1)).each{ int rowNumber ->
                    parseRow(currentSheet.getRow(rowNumber), dividerIndex)
                }
            }
        }
    }

    List<String> defaultOfferAreaNamesForDividers = [
            'Not Specified',
            'Not Specified',
            'Other FindOut time',
            'Other time'
    ]

    private void parseRow(Row row, int dividerIndex){
        String activityName = getStringValue(row.getCell(INDEX_ACTIVITY_NAME))?.trim()
        String offerAreaName = getStringValue(row.getCell(INDEX_OFFER_AREA_NAME))?.trim()

        OfferArea offerArea = OfferArea.findByName(offerAreaName)// Don't create an offer area if there's no data for it
        Activity activity = Activity.findByName(activityName) // Don't create an activity if there's no data for it

        if(activityName){
            def activityDataRange = getActivityDataRange(INDEX_ACTIVITY_DATA_START, getDaysInMonth(sheetDate))

            activityDataRange.each{ int columnIndex ->
                Date workdayDate = sheetDate.plusDays(columnIndex).toDate()

                // Get activity hours from cell
                double hours = getActivityHour(row.getCell(columnIndex))

                if(hours > 0){
                    // Now that we have data, create an activity and offer area if we didn't have one
                    offerArea = (offerAreaName == null) ? findOrSaveOfferAreaByName(defaultOfferAreaNamesForDividers[dividerIndex]) : findOrSaveOfferAreaByName(offerAreaName)
                    activity ? activity : findOrSaveActivityByNameAndOfferArea(activityName, offerArea)

                    createAndSaveWorkday(activity, workdayDate, hours)
                } else {
                    // If activity hours == 0 remove the workday if one exists
                    deleteWorkday(activity, workdayDate)
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

        // Does the file contain a username?
        if(!USER){
            excelFileOk = false
            println "No username found in file: '$FILENAME'"
        }

        EXCEL_FILE_OK = excelFileOk
    }

    private createTimeReportMonths(){
        Sheet myDashboard = EXCEL_FILE.getSheetAt(0)
        DateTime FirstSheetDate = new DateTime(getCell(EXCEL_FILE.getSheetAt(FIRST_REPORT_SHEET), DATE_CELL).dateCellValue)

        MONTHS_IN_YEAR.times { timeReportMonthIndex ->
            Cell standardTimeCell = getCell(myDashboard, [row: 5, column: (1+timeReportMonthIndex)])

            if(standardTimeCell.cellType == 2){
                int standardTime = standardTimeCell.cellFormula.split('\\*')[1].toInteger()

                TimeReportMonth.findOrSaveByDateAndStandardTime(FirstSheetDate.plusMonths(timeReportMonthIndex).toDate(), standardTime)
            }
        }
    }

    private double getActivityHour(Cell cell){
        // Get hours value and round to 2 decimals
        (cell && cell.getCellType() in NUMBER_TYPES ) ? cell.numericCellValue.round(2) : 0
    }

    private deleteWorkday(Activity activity, Date date){
        if(activity){
            Workday.findByUserAndActivityAndDate(USER, activity, date)?.delete()
        }
    }

    private int getTimereportYear(){
        Sheet sheet = EXCEL_FILE.getSheetAt(FIRST_REPORT_SHEET)
        return new DateTime(getCell(sheet, DATE_CELL).dateCellValue).getYear()
    }

    private String getStringValue(Cell cell){
        // Trim those pesky whitespaces!
        (cell?.getCellType() == STRING_TYPE) ? cell.getStringCellValue().trim() : null
    }

    private static Activity findOrSaveActivityByNameAndOfferArea(String activityName, OfferArea offerArea){
        activityName ? Activity.findOrSaveByNameAndOfferArea(activityName, offerArea) : null
    }

    private OfferArea findOrSaveOfferAreaByName(String offerAreaName){
        OfferArea offerArea = offerAreas.find{it.name == offerAreaName}
        if(!offerArea){
            offerArea = OfferArea.findOrCreateByName(offerAreaName)
            offerArea.save()
            offerAreas << offerArea
        }

        offerArea
    }

    private void createAndSaveWorkday(activity, date, hours){
        Workday workday = Workday.findOrCreateWhere(
                user: USER,
                date: date,
                activity: activity
        )

        workday.hours = hours
        workday.save()
    }

    private static Range getActivityDataRange(indexStart, daysInMonth){
        (indexStart..indexStart+daysInMonth-1)
    }

    private static int getDaysInMonth(DateTime date){
        date.dayOfMonth().getMaximumValue()
    }

    private void setUser(){
        String userName = getUserName()

        USER = userName ? User.findOrSaveWhere(name: userName) : null
    }

    private String getUserName(){
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
