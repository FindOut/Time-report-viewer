package backend

import grails.transaction.Transactional
import grails.util.Holders

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@Transactional
class ImportDataService {

    def fileService

    def importDataFromDropbox() {
        String url = Holders.config.dropbox.time_report.folder.url
        ZipFile zipFile = fileService.downloadZipFromUrl(url)

        zipFile.entries().each { ZipEntry entry ->
            List entryNameParts = entry.name.split('/')

            if(entryNameParts.size() > 1 && !entry.name.contains('MACOSX')){
                String fileName = entryNameParts[-1]

                if(fileService.isFileTimeReport(fileName)){
                    TimereportParser_default defaultParser = new TimereportParser_default(zipFile.getInputStream(entry), fileName)
                    defaultParser.parseWorkbook()
                }
            }
        }
        zipFile.close()
    }
}
