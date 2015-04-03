package backend

import grails.transaction.Transactional

import java.util.zip.ZipFile

@Transactional
class FileService {

    File createFileFromParams(fileParts, String webRootDir) {
        File userDir = new File(webRootDir)
        userDir.mkdirs()
        File localFile = new File(userDir, fileParts.originalFilename as String)
        fileParts.transferTo(localFile)

        return localFile
    }

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
}
