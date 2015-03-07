import backend.User
import grails.converters.JSON

class BootStrap {

    def init = { servletContext ->
        JSON.registerObjectMarshaller(User){
            return [
                id: it.id,
                name: it.name
            ]
        }
    }
    def destroy = {
    }
}
