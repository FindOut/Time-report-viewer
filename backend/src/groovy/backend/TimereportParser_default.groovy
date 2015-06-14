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
    private int TIMEREPORT_PARSER_YEAR = 2014
    // Index of first sheet with a date
    private int FIRST_REPORT_SHEET= 1
    // Location of the date cell for each report month
    private Map DATE_CELL = [row: 2,column: 1]
    // Location of the username for each report month
    private Map USER_NAME_CELL = [row: 1,column: 10]
    // Index of the column where activity name is found
    private int INDEX_OFFER_AREA_NAME = 0
    private int INDEX_ACTIVITY_NAME = 1
    private int INDEX_ACTIVITY_DATA_START = 3
    // Activities to ignore
    private List IGNORED_ACTIVITIES = [
            'Debiterbar tid per EO',
            'Investerad tid i EO ',
            '<fyll i aktivitet 1 h�r>',
            '< osv.. >',
            'Annan FindOut tid ',
            '. . .',
            'Annan tid',
    ]
    private List ACTIVITY_SEGMENTS = [
            'Debiterbar tid per EO',
            'Investerad tid i EO ',
            'Annan FindOut tid ',
            'Annan tid'
    ]

    // String indicating when to stop iterating over activities
    private String END_OF_DATA_ROWS = 'Summa normaltid'

    // Housekeeping
    private FILENAME = ''
    private int INTEGER_TYPE = 0
    private int STRING_TYPE = 1
    private int MONTHS_IN_YEAR = 12
    private Boolean ITERATING_OVER_ACTIVITY = false

    // For performance
    private offerAreas = OfferArea.list()


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

    void parseWorkbook(){
        if(EXCEL_FILE_OK){
            (1..MONTHS_IN_YEAR).each{ int sheetIndex ->
                Sheet sheet = EXCEL_FILE.getSheetAt(sheetIndex)

                // Get date on sheet
                DateTime sheetDate = new DateTime(getCell(sheet, DATE_CELL).dateCellValue)
                int daysInMonth = getDaysInMonth(sheetDate)

                def activityDataRange = getActivityDataRange(INDEX_ACTIVITY_DATA_START, daysInMonth)

                // Get activities
                Iterator<Row> rows = sheet.rowIterator()
                rows.eachWithIndex { Row row, int rowIndex ->
                    String activityName = getStringValue(row.getCell(INDEX_ACTIVITY_NAME))
                    String offerAreaName = getStringValue(row.getCell(INDEX_OFFER_AREA_NAME))

                    if (ITERATING_OVER_ACTIVITY && !IGNORED_ACTIVITIES.contains(activityName)){
                        OfferArea offerArea = (offerAreaName == null) ? findOrSaveOfferAreaByName('Not Specified') : findOrSaveOfferAreaByName(offerAreaName)
                        Activity activity = findOrSaveActivityByNameAndOfferArea(activityName, offerArea)

                        if(activity){
                            // Create and save a workday for each date on activity row
                            activityDataRange.eachWithIndex { int columnIndex, int index ->
                                Date workdayDate = sheetDate.plusDays(index).toDate()

                                // Get activity hours from cell
                                double hours = getActivityHour(row.getCell(columnIndex))

                                if(hours > 0){
                                    createAndSaveWorkday(activity, workdayDate, hours)
                                } else {
                                    // If activity hours == 0 remove the workday if one exists
                                    deleteWorkday(activity, workdayDate)
                                }
                            }
                        }
                    }

                    // Check if next row will be an activity row
                    setIteratingOverActivityRows(activityName)
                }
            }
        }
    }

    private verifyTimereport(){
        int fileYear = getTimereportYear()
        Boolean excelFileOk = true

        // Is the correct parser used?
        if(!fileYear == TIMEREPORT_PARSER_YEAR){
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

    private double getActivityHour(Cell cell){
        // Get hours value and round to 2 decimals
        (cell && cell.getCellType() == INTEGER_TYPE ) ? cell.numericCellValue.round(2) : 0
    }

    private deleteWorkday(Activity activity, Date date){
        Workday.findByUserAndActivityAndDate(USER, activity, date)?.delete()
    }

    private int getTimereportYear(){
        Sheet sheet = EXCEL_FILE.getSheetAt(FIRST_REPORT_SHEET)
        return new DateTime(getCell(sheet, DATE_CELL).dateCellValue).getYear()
    }

    private String getStringValue(Cell cell){
        // Trim those pesky whitespaces!
        (cell?.getCellType() == STRING_TYPE) ? cell.getStringCellValue().trim() : null
    }

    private Activity findOrSaveActivityByNameAndOfferArea(String activityName, OfferArea offerArea){
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

    private setUser(){
        String userName = getUserName()

        USER = userName ? User.findOrSaveWhere(name: userName) : null
    }

    private String getUserName(){
        String name
        // Get name from 'Namn' fields in sheet
        (1..MONTHS_IN_YEAR).each{ int sheetIndex ->
            Sheet sheet = EXCEL_FILE.getSheetAt(sheetIndex)
            name = name?: getCell(sheet, USER_NAME_CELL).stringCellValue
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

    private void setIteratingOverActivityRows(String activityName) {
        // Remove trailing whitespaces
        activityName = activityName?.trim()

        // Set ITERATING_OVER_ACTIVITY to true if we have entered the activity segments
        if (ACTIVITY_SEGMENTS.contains(activityName)){
            ITERATING_OVER_ACTIVITY = true
        } else if (activityName == END_OF_DATA_ROWS ){
            ITERATING_OVER_ACTIVITY = false
        }
    }
}
