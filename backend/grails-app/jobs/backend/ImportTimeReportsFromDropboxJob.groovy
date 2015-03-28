package backend

class ImportTimeReportsFromDropboxJob {
    static triggers = {
        cron name: 'importData', cronExpression: "0 0 4 * * ?"
    }

    def importDataService
    def execute() {
        importDataService.importDataFromDropbox()
    }
}
