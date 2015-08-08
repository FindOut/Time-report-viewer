package backend

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.joda.time.DateTime

class ExportController {

    def exportService

    def profitabilityBasis() {
        DateTime startOfFiscalYear = new DateTime().withTimeAtStartOfDay().minusYears(1).withDayOfMonth(1).withMonthOfYear(5)

        Map offerAreaMapping = [
                'Prod': 'D - Produktutveckling',
                'Proc': 'D - Processutveckling',
                'VU': 'D - Verktygsutveckling',
                'Prod-I': 'I - Produktutveckling',
                'Proc-I': 'I - Processutveckling',
                'VU-INV': 'I - Verktygsutveckling'
        ]

        Workbook excelFile = new HSSFWorkbook()
        excelFile.createSheet('Utfall timmar intäkter')

        Sheet sheet = excelFile.getSheetAt(0)
        exportService.createWorkArea(sheet, 45, 15)
        exportService.setSheetStyle(sheet)


        // create a template for the xml file to be filled in

        sheet.getRow(1).getCell(0).setCellValue('Utfall timmar')
        sheet.getRow(2).getCell(1).setCellValue('Timmar')
        sheet.getRow(2).getCell(2).setCellValue('Ackumulerat hittills')

        sheet.getRow(3).getCell(1).setCellValue('Timmar per månad')
        sheet.getRow(4).getCell(1).setCellValue('Buget')
        sheet.getRow(5).getCell(1).setCellValue('Rapporterade timmar')
        sheet.getRow(6).getCell(1).setCellValue('Diff Budget/Utfall')

        sheet.getRow(7).getCell(0).setCellValue('Summering')
        sheet.getRow(8).getCell(1).setCellValue('Rapporterade timmar')

        sheet.getRow(11).getCell(0).setCellValue('EOn')

        sheet.getRow(18).getCell(0).setCellValue('Annan FindOut tid')
        sheet.getRow(19).getCell(1).setCellValue('Varav OH')
        sheet.getRow(20).getCell(1).setCellValue('Headcounts på OH')
        sheet.getRow(21).getCell(1).setCellValue('Varav Kompetensutveckling')

        sheet.getRow(22).getCell(0).setCellValue('Annan tid')
        sheet.getRow(23).getCell(1).setCellValue('Varav Sjukdom, VAB, etc')
        sheet.getRow(24).getCell(1).setCellValue('Varav Semester')
        sheet.getRow(25).getCell(1).setCellValue('Varav Föräldrarledighet ')

        List timereportMonths = TimeReportMonth.withCriteria {
            between('date', startOfFiscalYear.toDate(), startOfFiscalYear.plusYears(1).minusDays(1).toDate())
        }.sort {
            it.date
        }


        // gets monthly data for offerAreas and activities
        12.times { monthIndex ->
            DateTime iteratingMonth = startOfFiscalYear.plusMonths(monthIndex)
            TimeReportMonth reportMonth = timereportMonths[monthIndex]

            List workdayActivities = Workday.withCriteria {
                between('date', iteratingMonth.toDate(), iteratingMonth.plusMonths(1).minusDays(1).toDate())

                projections {
                    groupProperty('activity')
                    sum('hours')
                }
            }



            double vacation = workdayActivities.find{it[0].name == 'Semester'}?.getAt(1) ?: 0
            double parentalLeave = workdayActivities.findAll{it[0].name.contains('Föräldrarledig')}*.getAt(1).sum() ?: 0
            double sickness =  workdayActivities.find{it[0].name == 'Sjukdom'}?.getAt(1) ?:0
            double vab = workdayActivities.find{it[0].name == 'VAB'}?.getAt(1)?:0
            double skillsDevelopment = workdayActivities.find{it[0].name == 'Kompetensutveckling'}?.getAt(1)?:0

            double budget = UserTimeReportMonth.withCriteria {
                between('timeReportMonth', iteratingMonth.toDate(), iteratingMonth.plusMonths(1).minusDays(1).toDate())

                projections {
                    sum('standardTime')
                }
            }[0] ?: 0
            double reportedHours = workdayActivities.inject(0){result, workdayActivity -> result + workdayActivity[1]}
            double reportedHoursBudgetDiff = reportedHours - budget

            Map offerAreas = exportService.getOfferAreas(workdayActivities)

            // write monthly data to excel

            sheet.getRow(2).getCell(3+monthIndex).setCellValue(iteratingMonth.monthOfYear().asShortText)
            sheet.getRow(3).getCell(3+monthIndex).setCellValue(reportMonth.standardTime)
            sheet.getRow(4).getCell(3+monthIndex).setCellValue(budget)
            sheet.getRow(5).getCell(3+monthIndex).setCellValue(reportedHours)
            sheet.getRow(6).getCell(3+monthIndex).setCellValue(reportedHoursBudgetDiff)

            sheet.getRow(8).getCell(3+monthIndex).setCellValue(reportedHours)

            offerAreaMapping.eachWithIndex{ offerArea, index ->
                sheet.getRow(12+index).getCell(1).setCellValue(offerArea.value)
                sheet.getRow(12+index).getCell(3+monthIndex).setCellValue(offerAreas[offerArea.key])
            }

            sheet.getRow(21).getCell(3+monthIndex).setCellValue(skillsDevelopment)

            sheet.getRow(23).getCell(3+monthIndex).setCellValue(sickness + vab)
            sheet.getRow(24).getCell(3+monthIndex).setCellValue(vacation)
            sheet.getRow(25).getCell(3+monthIndex).setCellValue(parentalLeave)

        }

        println "Kompetensutveckling" + exportService.getSkillsDevelopment(startOfFiscalYear)


        int monthStandardTime = TimeReportMonth.withCriteria {
            between('date', startOfFiscalYear.toDate(), startOfFiscalYear.plusMonths(1).minusDays(1).toDate())

            projections {
                property('standardTime')
            }
        }[0]

        response.setHeader("Content-disposition", /attachment; filename=input till lonsamhetsmodell.xls/)
        response.contentType = 'application/excel'
        excelFile.write(response.outputStream)
        response.outputStream.flush()
        response.outputStream.close()
    }
}
