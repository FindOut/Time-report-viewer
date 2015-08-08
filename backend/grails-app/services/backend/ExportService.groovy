package backend

import grails.transaction.Transactional
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.joda.time.DateTime

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


    def getReportedHoursForMonth(DateTime firstOfMonth){
        Long reportedHours = Workday.withCriteria {
            between('date', firstOfMonth.toDate(), firstOfMonth.plusMonths(1).minusDays(1).toDate())

            projections{
                sum('hours')
            }
        }[0]
    }

    def getOfferAreaDataForMonth(DateTime firstOfMonth){
        Workday.withCriteria {
            between('date', firstOfMonth.toDate(), firstOfMonth.plusMonths(1).minusDays(1).toDate())

            projections {
                activity {
                    offerArea {
                        groupProperty('name')
                    }
                }

                sum('hours')
            }
        }
    }

    def getSkillsDevelopment(DateTime firstOfMonth){
        Workday.withCriteria {
            between('date', firstOfMonth.toDate(), firstOfMonth.plusMonths(1).minusDays(1).toDate())

            activity{
                eq('name', 'Kompetensutveckling')
            }

            projections {
                sum('hours')
            }
        }
    }

    CellStyle createColoredStyle(Workbook workbook, String color){
        CellStyle style = workbook.createCellStyle()

        style.setFillForegroundColor(IndexedColors."$color".getIndex())
        style.setFillPattern(CellStyle.SOLID_FOREGROUND)

        style
    }

    def createWorkArea(Sheet sheet, int rows, int columns) {
        CellStyle yellowBG = createColoredStyle(sheet.getWorkbook(), 'YELLOW')
        CellStyle blueBG = createColoredStyle(sheet.getWorkbook(), 'PALE_BLUE')
        CellStyle greenBG = createColoredStyle(sheet.getWorkbook(), 'LIME')
        CellStyle gray25BG = createColoredStyle(sheet.getWorkbook(), 'GREY_25_PERCENT')

        rows.times { rowIndex ->
            sheet.createRow(rowIndex)
            Row row = sheet.getRow(rowIndex)

            columns.times { columnIndex ->
                row.createCell(columnIndex)

                if(columnIndex == 2 && rowIndex > 1){
                    row.getCell(columnIndex).setCellStyle(gray25BG)
                }

                if(rowIndex == 1 && columnIndex <= 14) {
                    row.getCell(columnIndex).setCellStyle(yellowBG)
                }

                if(rowIndex in [7, 11, 18, 22, 26] && columnIndex <= 14){
                    row.getCell(columnIndex).setCellStyle(blueBG)
                }

                if((rowIndex in [2,3, 4]) && columnIndex in (3..14)){
                    row.getCell(columnIndex).setCellStyle(gray25BG)
                }

                if((rowIndex in [5, 6]) && columnIndex in (3..14)){
                    row.getCell(columnIndex).setCellStyle(greenBG)
                }
            }
        }
    }

    def setSheetStyle(Sheet sheet){

        sheet.setColumnWidth(1, (30*256))
    }
}
