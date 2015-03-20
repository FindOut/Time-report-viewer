
package backend

import grails.transaction.Transactional

@Transactional
class ImportDataService {

    def excelFileParserService
    def dropboxService

    def importDataFromDropbox() {
        String timeReportsPath = "/FindOut- Linje/Tidrapporter/2014 - Tidrapporter"

        List files = dropboxService.downloadFiles(timeReportsPath)

        files.each{ File file ->
            if(file){
                excelFileParserService.parseFile(file)
                file.delete() // This does not seem to work
            }
        }
    }
}
