package backend

import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.Sql

class PageController {
    def dataSource
    def importDataService

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def index() {
        render view: '../index'
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def reloadDB(){
        Sql sql = new Sql(dataSource.getConnection())
        List applicationDomainObjects = grailsApplication.domainClasses

        sql.execute("SET FOREIGN_KEY_CHECKS=0")

        applicationDomainObjects.each{ object ->
            object.clazz.executeUpdate("delete from ${object.getName()}")
        }

        sql.execute("SET FOREIGN_KEY_CHECKS=1")

        importDataService.importDataFromDropbox()
    }
}
