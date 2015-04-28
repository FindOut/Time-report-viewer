package backend

import grails.transaction.Transactional

import java.util.zip.ZipFile

@Transactional
class FileService {
    ZipFile downloadZipFromUrl(String url){
        new File("temp/TimeReports.zip").withOutputStream { out ->
            try {
                new URL(url).withInputStream { from ->  out << from; }
            } catch (e) {
                println 'Bad url: ' + url
                throw e
            }
        }.close()

        new ZipFile(new File('temp/TimeReports.zip'))
    }

    static Boolean isFileTimeReport(String fileName){
        fileName = fileName.toLowerCase()
        Boolean isTimeReport = true

        isTimeReport = isTimeReport && fileName.contains(' - tidrapport')
        isTimeReport = isTimeReport && fileName.contains('.xls')

        isTimeReport = isTimeReport && !fileName.contains('konfliktkopia')
        isTimeReport = isTimeReport && !fileName.contains('conflicted copy')
        isTimeReport = isTimeReport && !fileName.contains('justering')

        return isTimeReport
    }
}
