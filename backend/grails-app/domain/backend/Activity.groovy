package backend

import grails.rest.Resource

@Resource(uri='/api/activities')
class Activity {

    String name

    static constraints = {
        name nullable: false, unique: true
    }
}
