package backend

import grails.plugin.springsecurity.annotation.Secured

class PageController {
    def dataSource
    def sessionFactory
    def importDataService

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
