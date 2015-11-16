package backend

import grails.transaction.Transactional
import grails.util.Holders
import groovy.sql.Sql

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@Transactional
class ImportDataService {
    static transactional = false

    def dataSource
    def sessionFactory
    def fileService

    def clearDatabase(){
        Sql sql = new Sql(dataSource.getConnection())
        List applicationDomainObjects = Holders.getGrailsApplication().domainClasses*.clazz

        sql.withBatch { sqlBatch ->
            sqlBatch.addBatch("SET FOREIGN_KEY_CHECKS=0")

            applicationDomainObjects.each{ object ->
                String tableName = sessionFactory.getClassMetadata(object).tableName
                sqlBatch.addBatch("Truncate table $tableName")
            }

            // This needs to be done explicitly since there is no domain object for it
            sqlBatch.addBatch("Truncate table monthly_report_activity_report")
            sqlBatch.addBatch("SET FOREIGN_KEY_CHECKS=1")
        }
    }

    def importDataFromDropbox() {

        // This needs to be done when loading data from the time reports. The reason for this is that the application is
        // not updating existing data properly.
        clearDatabase()


        String url = Holders.config.dropbox.time_report.folder.url
        ZipFile zipFile = fileService.downloadZipFromUrl(url)

        zipFile.entries().each { ZipEntry entry ->
            List entryNameParts = entry.name.split('/')

            if(entryNameParts.size() > 1 && !entry.name.contains('MACOSX') && !entry.name.contains('conflicted')){
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
