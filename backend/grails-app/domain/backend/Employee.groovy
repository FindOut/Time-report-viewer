package backend

import grails.rest.Resource

@Resource(uri='/api/employees')
class Employee {
    String name

    static constraints = {
        name nullable: false, unique: true
    }
}
