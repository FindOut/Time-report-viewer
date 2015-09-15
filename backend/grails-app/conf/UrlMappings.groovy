class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        '/clearDB'(controller: 'page', action: 'clearDB')
        "/api/activityReports"(resources: 'activityReport')
        "/api/export/$action"(controller: 'export')
        "/api/import/$action"(controller: 'importData')
        "/"(controller: 'page')

        "500"(view:'/error')
	}
}
