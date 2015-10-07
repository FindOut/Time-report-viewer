package backend

import grails.rest.Resource

@Resource(uri='/api/activities')
class Activity { // Activities has a custom JSON marshaller

    String name

    ActivityType activityType
    Customer customer
    OfferArea offerArea

    static mapping = {
        name index: 'activity__name_idx'
        activityType index: 'activity__activity_type_idx'
        customer index: 'activity__customer_idx'
        offerArea index: 'activity__offerArea_idx'
    }

    static constraints = {
        name nullable: false

        activityType nullable: true
        customer nullable: true
        offerArea nullable: true
    }
}
