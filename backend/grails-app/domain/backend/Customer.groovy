package backend

class Customer {

    static hasMany = [activities: Activity]

    String name

    static constraints = {
        name nullable: false
    }
}
