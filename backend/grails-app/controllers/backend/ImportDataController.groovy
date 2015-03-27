package backend

class ImportDataController {

    def excelFileParserService
    def fileService
    def importDataService

    def importDataFromDropbox() {
        importDataService.importDataFromDropbox()

        render status:200
    }

    def importData() {
        File file = fileService.createFileFromParams(params.file, servletContext.getRealPath("/"))
        excelFileParserService.parseFile(file)

        render text: 'test'
    }
}
