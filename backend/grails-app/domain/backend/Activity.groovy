package backend

import grails.rest.Resource

@Resource(uri='/api/activities')
class Activity { // Activities has a custom JSON marshaller

    String name

    ActivityType activityType
    Customer customer
    OfferArea offerArea

    static constraints = {
        name nullable: false

        activityType nullable: true
        customer nullable: true
        offerArea nullable: true
    }
}
