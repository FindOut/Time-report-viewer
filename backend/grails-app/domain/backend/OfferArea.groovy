package backend

import grails.rest.Resource

@Resource(uri='/api/offerAreas')
class OfferArea {

    static hasMany = [activities: Activity]

    String name
    String typeOfInvestment

    static constraints = {
        typeOfInvestment nullable: true
    }

    String toString(){
        name
    }
}
