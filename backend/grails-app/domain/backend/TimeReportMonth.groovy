package backend

class TimeReportMonth {

    Date date
    int standardTime

    static constraints = {
        date nullable: false
        standardTime nullable: false
    }
}
