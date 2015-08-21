package backend

class MonthlyReport {

    static belongsTo = [employee: Employee]
    static hasMany = [activityReports: ActivityReport]

    Date date
    double standardTime

    static constraints = {
    }
}
