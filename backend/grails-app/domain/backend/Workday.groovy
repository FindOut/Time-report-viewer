package backend

import grails.rest.Resource

@Resource(uri='/api/workday')
class Workday {

    static belongsTo = [user: User]

    Activity activity
    Date date
    double hours

    static constraints = {
        user nullable: false
        activity nullable: false
        date nullable: false
    }
}
