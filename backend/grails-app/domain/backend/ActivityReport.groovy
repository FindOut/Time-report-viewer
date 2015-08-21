package backend

import grails.rest.Resource

@Resource(uri='/api/activityReports')
class ActivityReport {

    static belongsTo = [
            employee: Employee
    ]

    Activity activity
    Date date
    double hours

    static constraints = {
        employee nullable: false
        activity nullable: false
        date nullable: false
    }
}
