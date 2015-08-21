class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        '/clearDB'(controller: 'page', action: 'clearDB')
        "/api/activityReports"(resources: 'activityReport')
        "/"(controller: 'page')

        "500"(view:'/error')
	}
}
