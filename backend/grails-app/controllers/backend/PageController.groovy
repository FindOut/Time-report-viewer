package backend

class PageController {

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
