package backend

import grails.plugin.springsecurity.annotation.Secured

class PageController {
    @Secured(['IS_AUTHENTICATED_FULLY'])
    def index() {
        render view: '../index'
    }

    def clearDB(){
        Workday.executeUpdate('delete from Workday')
        User.executeUpdate('delete from User')
        Activity.executeUpdate('delete from Activity')

        render status: 200

    }
}
