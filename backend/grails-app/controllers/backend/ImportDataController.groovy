package backend

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.multipart.commons.CommonsMultipartFile

class ImportDataController {

    def fileService
    def importDataService

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def importDataFromDropbox() {
        importDataService.importDataFromDropbox()

        render status:200
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def importData() {
        CommonsMultipartFile file = request.getFile('file')

        if (fileService.isFileTimeReport(file.originalFilename)){
            TimereportParser_2014 parser2014 = new TimereportParser_2014(file.inputStream, file.originalFilename)
            parser2014.parseWorkbook()
        }

        render text: 'test'
    }
}
