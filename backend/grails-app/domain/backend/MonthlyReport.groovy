package backend

class MonthlyReport {

    static belongsTo = [employee: Employee]
    static hasMany = [activityReports: ActivityReport]

    Date date
    double standardTime

    static mapping = {
        employee index: 'monthly_report_employee_idx'
        activityReports index: 'monthly_report_activity_reports_idx'
    }

    static constraints = {
    }
}
