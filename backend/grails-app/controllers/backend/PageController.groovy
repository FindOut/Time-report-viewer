package backend

import grails.plugin.springsecurity.annotation.Secured

class PageController {
    def importDataService

    def fetchFile(String serverFileName){
        File file = new File("temp/$serverFileName")
        String fileName = serverFileName.split('_')[1]

        response.setContentType("application/octet-stream")
        response.setContentType("application/download")
        response.setHeader("Content-disposition", "attachment; filename=$fileName")
        response.outputStream << file.bytes
        response.outputStream.flush()
        response.outputStream.close()

        file.delete()
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def index() {
        render view: '../index'
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def reloadDB(){
        importDataService.importDataFromDropbox()

        render status:200
    }
}
