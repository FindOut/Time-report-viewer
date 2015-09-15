import backend.Activity
import backend.ActivityReport
import grails.converters.JSON
import grails.util.Holders

class BootStrap {

    def init = { servletContext ->
        println "###"*40
        println "###"*40
        println Holders.config.dropbox.time_report.folder.url
        println "###"*40
        println "###"*40
        JSON.registerObjectMarshaller(Activity) {
            return [
                    id: it.id,
                    name: it.name,
                    offerArea: [
                            id: it.offerArea.id,
                            name: it.offerArea.name
                    ]
            ]
        }
        JSON.registerObjectMarshaller(ActivityReport) {
            return [
                    id: it.id,
                    date: it.date,
                    hours: it.hours,
                    activity: [
                            id: it.activity.id,
                            name: it.activity.name
                    ],
                    employee: [
                            id: it.employee.id,
                            name: it.employee.name
                    ]
            ]
        }
    }
    def destroy = {
    }
}
