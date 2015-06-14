package backend

import grails.rest.Resource

@Resource(uri='/api/activities')
class Activity { // Activities has a custom JSON marshaller

    String name

    OfferArea offerArea

    static constraints = {
        name nullable: false
        offerArea nullable: true
    }
}
