package backend

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.joda.time.DateTime

class ImportDataController {


    String accessToken = 'ZFMLm8JBu2kAAAAAAAAUHpJGwM9dNMSkoDxE92O-2aAW1-zY37Rzmy0NlOfjCmDp'
    def dropboxService
    def excelFileParserService

    def importDropboxFiles() {
        String timeReportsPath = "/FindOut- Linje/Tidrapporter/2014 - Tidrapporter"

        List files = dropboxService.downloadFiles(timeReportsPath)

        files.each{ File file ->

            if(file){
                println 'parsing file: ' + file.getName()
                excelFileParserService.parseFile(file)
            }
        }
        println files
//        files.each{
//            if (it.isFile()){
//                DbxEntry.File dbxEntryFile = it as DbxEntry.File
//                String fileName = dbxEntryFile.path.replaceAll("(?i)"+timeReportsPath + '/', '')
//                FileOutputStream outputStream = new FileOutputStream(fileName);
//
//                outputStream.close()
//
//                excelService.parseFile(fileName)
//                try {
//                    if (file.exists()) {
////                        file.delete() // why this no work!?!
//                    }
//                }
//                catch(IOException e) {
//                    System.out.print("Error occured!"+ e);
//                }
//            }
////            FileInputStream excelFileStream = new FileInputStream(downloadedFile)
////            Workbook excelFile = WorkbookFactory.create(excelFileStream)
////            int nrOfSheets = excelFile.numberOfSheets
//        }
    }

    def importData() {
        def file = params.file
        def webRootDir = servletContext.getRealPath("/")

        File userDir = new File(webRootDir)
        userDir.mkdirs()
        File localFile = new File(userDir, file.originalFilename as String)
        file.transferTo(localFile)
        FileInputStream excelFileStream = new FileInputStream(localFile)

        Workbook excelFile = WorkbookFactory.create(excelFileStream)
        int nrOfSheets = excelFile.numberOfSheets

        // Get workbook user
        String name = getUserName(excelFile)
        if (!name) {
            name = localFile.getName().split(' - ')[0].trim()
        }
        User user = User.findOrSaveWhere(name: name)

        (1..MONTHS_IN_YEAR).each { int sheetIndex ->
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
                if (activityName) {
                    // Remove trailing whitespaces
                    activityName = activityName.trim()
                    // Currently iterating over activities?
                    checkActivity = iteratingOverActivityRows(activityName) ?: checkActivity

                    if (checkActivity && !IGNORED_ACTIVITIES.contains(activityName)) {
                        // Get activity
                        Activity activity = findOrCreateActivity(activityName)

                        // Create and save a workday for each date on activity row
                        activityDataRange.eachWithIndex { int columnIndex, int index ->
                            Date date = sheetDate.plusDays(index).toDate()
                            double hours = 0
                            if (row.getCell(columnIndex) && row.getCell(columnIndex).getCellType() == 0) {
                                // Get hours value and round to 2 decimals
                                hours = row.getCell(columnIndex)?.numericCellValue
                                hours = hours.round(2)
                            }

                            if (hours > 0) {
                                createAndSaveWorkday(user, activity, date, hours)
                            }
                        }
                    }
                }
            }
        }
        println Workday.count()
        render text: 'test'
    }
}
