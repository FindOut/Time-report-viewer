package backend

import grails.rest.Resource

@Resource(uri='/activities')
class Activity {

    String name

    static constraints = {
        name nullable: false, unique: true
    }
}
