package backend

import grails.transaction.Transactional

@Transactional
class FileService {

    File createFileFromParams(fileParts, String webRootDir) {
        File userDir = new File(webRootDir)
        userDir.mkdirs()
        File localFile = new File(userDir, fileParts.originalFilename as String)
        fileParts.transferTo(localFile)

        return localFile
    }
}
