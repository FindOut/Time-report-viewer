package backend

class ImportTimeReportsFromDropboxJob {
    static triggers = {
        cron name: 'importData', cronExpression: "0 0 4 * * ?"
    }

    def execute() {
        def dropboxService = new DropboxService()
        dropboxService.importDataFromDropbox()
    }
}
