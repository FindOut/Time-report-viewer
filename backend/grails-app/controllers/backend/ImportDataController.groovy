package backend

class ImportDataController {

    def excelFileParserService
    def importDataService

    def importDataFromDropbox() {
        importDataService.importDataFromDropbox()
    }

    def importData() {
        File file = new File(params.file.originalFilename as String)
        params.file.transferTo(file)

        excelFileParserService.parseFile(file)

        render text: 'test'
    }
}
