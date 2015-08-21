package backend

import grails.plugin.springsecurity.annotation.Secured
import grails.rest.RestfulController
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class ActivityReportController extends RestfulController{
    static responseFormats = ['json', 'xml']

    ActivityReportController(){
        super(ActivityReport)
    }

    /*
     @params
     all .list() params

     Date from (yyyy-MM-dd)
     Date to (yyyy-MM-dd)
     List users
     List activities

     */
    @Secured(['IS_AUTHENTICATED_FULLY'])
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

        List offerAreaIds = []
        if(params['offerAreas[]']){
            params.list('offerAreas[]').each{
                offerAreaIds.push(it as Long)
            }
        }

        def criteria = ActivityReport.createCriteria()
        List activityReports = criteria.list(params){
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
            if(offerAreaIds){
                activity{
                    offerArea {
                        'in'('id', offerAreaIds)
                    }
                }
            }
        }

        respond activityReports

//        respond super.listAllResources(params), model: [("${super.resourceName}Count".toString()): super.countResources()]
    }
}
