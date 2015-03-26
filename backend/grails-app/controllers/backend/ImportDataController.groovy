package backend

class ImportDataController {

    def excelFileParserService
    def fileService
    def importDataService

    def importDataFromDropbox() {
        importDataService.importDataFromDropbox()
    }

    def importData() {
        File file = fileService.createFileFromParams(params.file, servletContext.getRealPath("/"))
        excelFileParserService.parseFile(file)

        render text: 'test'
    }
}
