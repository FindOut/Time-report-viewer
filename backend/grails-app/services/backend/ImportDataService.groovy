
package backend

import grails.transaction.Transactional
import grails.util.Holders

import java.util.zip.ZipFile

@Transactional
class ImportDataService {

    def excelFileParserService
    def fileService

    def importDataFromDropbox() {
        String url = Holders.config.dropbox.time_report.folder.url
        ZipFile zipFile = fileService.downloadZipFromUrl(url)

        excelFileParserService.parseFilesInZip(zipFile)
    }
}
