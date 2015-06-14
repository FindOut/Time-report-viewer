import backend.Activity
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
    }
    def destroy = {
    }
}
