package backend

import grails.plugin.springsecurity.annotation.Secured

class PageController {
    @Secured(['IS_AUTHENTICATED_FULLY'])
    def index() {
        render view: '../index'
    }

    def clearDB(){
        ActivityReport.executeUpdate('delete from ActivityReport')
        Employee.executeUpdate('delete from Employee')
        Activity.executeUpdate('delete from Activity')

        render status: 200

    }
}
