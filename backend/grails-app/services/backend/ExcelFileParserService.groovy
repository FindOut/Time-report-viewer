package backend

import grails.transaction.Transactional
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.joda.time.DateTime

@Transactional
class ExcelFileParserService {
    int STRING_TYPE = 1
    int MONTHS_IN_YEAR = 12
    int INDEX_ACTIVITY_NAME = 1
    int INDEX_ACTIVITY_DATA_START = 3
    Map USER_NAME_CELL = [row: 1,column: 10]
    Map DATE_CELL = [row: 2,column: 1]
    List IGNORED_ACTIVITIES = [
            'Debiterbar tid per EO',
            'Investerad tid i EO ',
            '<fyll i aktivitet 1 här>',
            '< osv.. >',
            'Annan FindOut tid ',
            '. . .',
            'Annan tid',
            'Totalsumma',
            'Kompledighet',
            'Summa normaltid',
            'Endast om särskild ersättning avtalats för övertid / obekväm arbetstid! Var noggrann och ange tidpunkt om det rör kvälls- eller helgarbete.'
    ]

    def parseFile(File file) {
//        File file = new File(fileName)


        Workbook excelFile = WorkbookFactory.create(file)
        int nrOfSheets = excelFile.numberOfSheets

        // Get workbook user
        String name = getUserName(excelFile)
        if (!name){
            name = file.getName().split(' - ')[0].trim()
        }
        User user = User.findOrSaveWhere(name: name)

        (1..MONTHS_IN_YEAR).each{ int sheetIndex ->
            Boolean checkActivity = false

            Sheet sheet = excelFile.getSheetAt(sheetIndex)

            // Get date on sheet
            DateTime sheetDate = new DateTime(getCell(sheet, DATE_CELL).dateCellValue)

            // Get days in month
            int daysInMonth = getDaysInMonth(sheetDate)

            def activityDataRange = getActivityDataRange(INDEX_ACTIVITY_DATA_START, daysInMonth)

            // Get activities
            Iterator<Row> rows = sheet.rowIterator()
            rows.eachWithIndex { Row row, int rowIndex ->
                String activityName = getActivityName(row)
                if (activityName){
                    // Remove trailing whitespaces
                    activityName = activityName.trim()
                    // Currently iterating over activities?
                    checkActivity = iteratingOverActivityRows(activityName) ?: checkActivity

                    if (checkActivity && !IGNORED_ACTIVITIES.contains(activityName)){
                        // Get activity
                        Activity activity = findOrCreateActivity(activityName)

                        // Create and save a workday for each date on activity row
                        activityDataRange.eachWithIndex { int columnIndex, int index ->
                            Date date = sheetDate.plusDays(index).toDate()
                            double hours = 0
                            if(row.getCell(columnIndex) && row.getCell(columnIndex).getCellType() == 0){
                                // Get hours value and round to 2 decimals
                                hours = row.getCell(columnIndex)?.numericCellValue
                                hours = hours.round(2)
                            }

                            if(hours > 0){
                                createAndSaveWorkday(user, activity, date, hours)
                            }
                        }
                    }
                }
            }
        }
    }

    private String getActivityName(Row row){
        if (row.getCell(1) && row.getCell(1).getCellType() == STRING_TYPE) {
            return row.getCell(INDEX_ACTIVITY_NAME).getStringCellValue()
        }
        return null
    }

    private static Activity findOrCreateActivity(String activityName){
        if(activityName){
            Activity activity = Activity.findByName(activityName)
            if(!activity?.id){
                activity = new Activity(name: activityName)
                activity.save(flush: true)

            }
            return activity
        }
        return null
    }

    private static void createAndSaveWorkday(user, activity, date, hours){
        Workday workday = Workday.findOrCreateWhere(
                user: user,
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

    private String getUserName(Workbook excelFile){
        String name
        (1..MONTHS_IN_YEAR).each{ int sheetIndex ->
            Sheet sheet = excelFile.getSheetAt(sheetIndex)
            name = name?: getCell(sheet, USER_NAME_CELL).stringCellValue
        }

        name
    }
    private static Cell getCell(Sheet sheet, map ){
        sheet.getRow(map.row).getCell(map.column)
    }

    private static Boolean iteratingOverActivityRows(String activityCell) {
        Boolean checkActivity = false
        if (activityCell == "Debiterbar tid per EO"){
            checkActivity = true
        } else if (activityCell.toLowerCase() == 'summa normaltid' ){
            checkActivity = false
        }
        checkActivity
    }
}
