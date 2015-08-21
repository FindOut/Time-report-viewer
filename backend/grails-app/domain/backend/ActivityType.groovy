package backend

class ActivityType {

    static hasMany = [activities: Activity]

    String name

    static constraints = {
        name nullable: false
    }
}
