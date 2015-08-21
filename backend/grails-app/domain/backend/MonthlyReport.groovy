package backend

class MonthlyReport {

    static belongsTo = [user: Employee]

    Date timeReportMonth
    double standardTime

    static constraints = {
    }
}
