import backend.Activity
import backend.Workday
import grails.converters.JSON

class BootStrap {

    def init = { servletContext ->
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
        JSON.registerObjectMarshaller(Workday) {
            return [
                    id: it.id,
                    date: it.date,
                    hours: it.hours,
                    activity: [
                            id: it.activity.id,
                            name: it.activity.name
                    ],
                    user: [
                            id: it.user.id,
                            name: it.user.name
                    ]
            ]
        }
    }
    def destroy = {
    }
}
