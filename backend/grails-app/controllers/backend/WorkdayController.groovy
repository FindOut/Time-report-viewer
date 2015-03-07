package backend

import grails.rest.RestfulController
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class WorkdayController extends RestfulController{
    static responseFormats = ['json', 'xml']

    WorkdayController(){
        super(Workday)
    }

    /*
     @params
     all .list() params

     Date from (yyyy-MM-dd)
     Date to (yyyy-MM-dd)
     List users
     List activities

     */
    def index() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        Date fromDate = params.from ? formatter.parseDateTime(params.from).toDate() : null;
        Date toDate = params.to ? formatter.parseDateTime(params.to).toDate() : null;

        List userIds = []
        if(params['users[]']){
            params.list('users[]').each{
                userIds.push(it as Long)
            }
        }


        List activityIds = []
        if(params['activities[]']){
            params.list('activities[]').each{
                activityIds.push(it as Long)
            }
        }

        def criteria = Workday.createCriteria()
        List workdays = criteria.list(params){
            if(fromDate){ge('date', fromDate)}
            if(toDate){le('date', toDate)}
            if(userIds){
                user{
                    'in'('id', userIds)
                }
            }
            if(activityIds){
                activity{
                    'in'('id', activityIds)
                }
            }

        }

        respond workdays

//        respond super.listAllResources(params), model: [("${super.resourceName}Count".toString()): super.countResources()]
    }
}
