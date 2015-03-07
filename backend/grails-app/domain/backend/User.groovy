package backend

import grails.rest.Resource

@Resource(uri='/users')
class User {
    String name

    static constraints = {
        name nullable: false, unique: true
    }
}
