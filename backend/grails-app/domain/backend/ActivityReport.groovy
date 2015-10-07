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

    static mapping = {
        activity index: 'activity_report_activity_idx'
        employee index: 'activity_report_employee_idx'
        date index: 'activity_report_date_idx'
        hours index: 'activity_report_hours_idx'
    }

    static constraints = {
        employee nullable: false
        activity nullable: false
        date nullable: false
    }
}
