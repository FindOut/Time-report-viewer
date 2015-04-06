package backend

import grails.plugin.springsecurity.annotation.Secured

class ImportDataController {

    def excelFileParserService
    def fileService
    def importDataService

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def importDataFromDropbox() {
        importDataService.importDataFromDropbox()

        render status:200
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def importData() {
        File file = fileService.createFileFromParams(params.file, servletContext.getRealPath("/"))
        excelFileParserService.parseFile(file)

        render text: 'test'
    }
}
