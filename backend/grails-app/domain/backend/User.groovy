package backend

import grails.rest.Resource

@Resource(uri='/api/users')
class User {
    String name

    static constraints = {
        name nullable: false, unique: true
    }
}
