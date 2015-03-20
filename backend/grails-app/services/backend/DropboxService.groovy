package backend

import com.dropbox.core.DbxClient
import com.dropbox.core.DbxEntry
import com.dropbox.core.DbxRequestConfig
import grails.transaction.Transactional

@Transactional
class DropboxService {
    String accessToken = 'ZFMLm8JBu2kAAAAAAAAUHpJGwM9dNMSkoDxE92O-2aAW1-zY37Rzmy0NlOfjCmDp'


    List downloadFiles(String timeReportsPath) {
        DbxRequestConfig config = new DbxRequestConfig(
                "TimeReportViewer/1.0", Locale.getDefault().toString());

        DbxClient client = new DbxClient(config, accessToken);

        List fileEntries = client.getMetadataWithChildren(timeReportsPath).children

        fileEntries.collect{ fileEntry ->
            String fileName = fileEntry.path.replaceAll("(?i)" + timeReportsPath + "/", '')
            println fileName
            println fileEntry.path
            if(fileName.contains('.xls')){
                downloadFile(fileEntry, fileName)
            }
        }
    }
    def downloadFile(fileEntry, String fileName) {
        DbxRequestConfig config = new DbxRequestConfig(
                "TimeReportViewer/1.0", Locale.getDefault().toString());

        DbxClient client = new DbxClient(config, accessToken);

        FileOutputStream outputStream = new FileOutputStream(fileName);
        try {
            DbxEntry.File downloadedFile = client.getFile(fileEntry.path, null, outputStream);

//            FileInputStream excelFileStream = new FileInputStream(downloadedFile)
//            Workbook excelFile = WorkbookFactory.create(excelFileStream)
//            int nrOfSheets = excelFile.numberOfSheets
//            println nrOfSheets
        } finally {
            outputStream.close();
        }
        new File(fileName)
    }
}
