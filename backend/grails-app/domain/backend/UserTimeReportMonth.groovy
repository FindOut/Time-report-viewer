package backend

class UserTimeReportMonth {

    static belongsTo = [user: User]

    Date timeReportMonth
    double standardTime

    static constraints = {
    }
}
