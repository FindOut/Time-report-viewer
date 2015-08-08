package backend

import grails.transaction.Transactional
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFFont

@Transactional
class ExportService {

    Map getOfferAreas(workdayActivities) {
        Map result = [:]

        workdayActivities.groupBy{
            it[0].offerArea.name
        }.collect {
            double offerAreaHours = it.value.inject(0) { sum, activityHours ->
                sum + activityHours[1]
            }

            result[(it.key)] = offerAreaHours
        }

        result
    }

    CellStyle createColoredStyle(Workbook workbook, int r, int b, int g){
        CellStyle style = workbook.createCellStyle()

        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(r,b,g)))
        style.setFillPattern(CellStyle.SOLID_FOREGROUND)

        style
    }

    def createWorkArea(Sheet sheet, int rows, int columns) {
        XSSFFont font = sheet.getWorkbook().createFont()
        XSSFFont boldFont = sheet.getWorkbook().createFont()
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD)

        CellStyle yellowBGBold = createColoredStyle(sheet.getWorkbook(), 255, 255, 0)
        yellowBGBold.setFont(boldFont)
        CellStyle blueBG = createColoredStyle(sheet.getWorkbook(), 0, 176, 240)
        CellStyle blueBGBold = createColoredStyle(sheet.getWorkbook(), 0, 176, 240)
        blueBGBold.setFont(boldFont)
        CellStyle greenBG = createColoredStyle(sheet.getWorkbook(), 146, 208, 80)
        CellStyle greenBGBold = createColoredStyle(sheet.getWorkbook(), 146, 208, 80)
        greenBGBold.setFont(boldFont)
        CellStyle blueishGray = createColoredStyle(sheet.getWorkbook(), 220, 230, 241)
        CellStyle blueishGrayBold = createColoredStyle(sheet.getWorkbook(), 220, 230, 241)
        blueishGrayBold.setFont(boldFont)
        blueishGrayBold.setWrapText(true)

        rows.times { rowIndex ->
            sheet.createRow(rowIndex)
            Row row = sheet.getRow(rowIndex)

            columns.times { columnIndex ->
                row.createCell(columnIndex)

                if(columnIndex == 2 && rowIndex == 2){
                    row.getCell(columnIndex).setCellStyle(blueishGrayBold)
                }
                if(columnIndex == 2 && rowIndex > 2){
                    row.getCell(columnIndex).setCellStyle(blueishGray)
                }

                if(rowIndex == 1 && columnIndex <= 14) {
                    row.getCell(columnIndex).setCellStyle(yellowBGBold)
                }

                if(rowIndex in [7, 11, 19, 23, 27] && columnIndex <= 14){
                    row.getCell(columnIndex).setCellStyle(blueBGBold)
                }

                if(rowIndex == 3 && columnIndex > 2 && columnIndex <= 14){
                    row.getCell(columnIndex).setCellStyle(blueBG)
                }

                if(rowIndex == 2 && columnIndex in (3..14)){
                    row.getCell(columnIndex).setCellStyle(greenBGBold)
                }

                if((rowIndex in [ 4, 5, 6]) && columnIndex in (3..14)){
                    row.getCell(columnIndex).setCellStyle(greenBG)
                }
            }
        }
    }

    def setSheetStyle(Sheet sheet){

        sheet.setColumnWidth(1, (30*256))
        sheet.setColumnWidth(2, (16*256))

        (3..14).each{
            sheet.setColumnWidth(2, (13*256))
        }

        sheet.getRow(2).setHeight(30*20 as short)
    }
}
