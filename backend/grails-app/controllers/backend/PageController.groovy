package backend

import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.Sql

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
        Sql sql = new Sql(dataSource.getConnection())
        List applicationDomainObjects = grailsApplication.domainClasses*.clazz




        sql.withBatch { sqlBatch ->
            sqlBatch.addBatch("SET FOREIGN_KEY_CHECKS=0")

            applicationDomainObjects.each{ object ->
                String tableName = sessionFactory.getClassMetadata(object).tableName
                sqlBatch.addBatch("Truncate table $tableName")
            }

            sqlBatch.addBatch("SET FOREIGN_KEY_CHECKS=1")
        }

        importDataService.importDataFromDropbox()

        render status:200
    }
}
