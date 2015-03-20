package backend

class ImportDataController {

    def dropboxService
    def excelFileParserService

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

    def importData() {
        def file = params.file
        def webRootDir = servletContext.getRealPath("/")

        File userDir = new File(webRootDir)
        userDir.mkdirs()
        File localFile = new File(userDir, file.originalFilename as String)
        file.transferTo(localFile)

        excelFileParserService.parseFile(localFile)

        render text: 'test'
    }
}
