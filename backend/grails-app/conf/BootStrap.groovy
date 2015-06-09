import backend.OfferArea
import backend.User
import grails.converters.JSON
import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils

class BootStrap {

    def init = { servletContext ->

        // create offer areas
        List offerAreas = [
                [ name: 'VU'],
                [ name: 'Proc'],
                [ name: 'Prod'],

                [ name: 'Prod-I', typeOfInvestment: 'i'],
                [ name: 'Proc-I', typeOfInvestment: 'i'],
                [ name: 'VU-?', typeOfInvestment: '?'],
                [ name: 'VU-INV', typeOfInvestment: 'i'],
                [ name: 'VU-PINV', typeOfInvestment: 'pinv'],
                [ name: 'VU-SPEK', typeOfInvestment: 'spek']
        ]
        offerAreas.each{
            new OfferArea(it).save()
        }
    }
    def destroy = {
    }
}
