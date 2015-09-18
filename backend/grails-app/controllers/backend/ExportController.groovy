package backend

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime

class ExportController {

    def exportService

    def profitabilityBasis() {
        DateTime startOfFiscalYear = new DateTime().withTimeAtStartOfDay().withDayOfMonth(1).withMonthOfYear(5)

        Map offerAreaMapping = [
                'Prod': 'D - Produktutveckling',
                'Proc': 'D - Processutveckling',
                'VU': 'D - Verktygsutveckling',
                'Prod-I': 'I - Produktutveckling',
                'Proc-I': 'I - Processutveckling',
                'VU-INV': 'I - Verktygsutveckling',
                'Not Specified': 'Ej specificerat'
        ]

        Workbook excelFile = new XSSFWorkbook()
        excelFile.createSheet('Utfall timmar intäkter')

        Sheet sheet = excelFile.getSheetAt(0)
        exportService.createWorkArea(sheet, 45, 15)
        exportService.setSheetStyle(sheet)


        // create a template for the xml file to be filled in

        sheet.getRow(1).getCell(0).setCellValue('Utfall timmar')
        sheet.getRow(2).getCell(1).setCellValue('Timmar')
        sheet.getRow(2).getCell(2).setCellValue('Ackumulerat hittills')

        sheet.getRow(3).getCell(1).setCellValue('Timmar per månad')
        sheet.getRow(4).getCell(1).setCellValue('Budget')
        sheet.getRow(5).getCell(1).setCellValue('Rapporterade timmar')
        sheet.getRow(6).getCell(1).setCellValue('Diff Budget/Utfall')

        sheet.getRow(7).getCell(0).setCellValue('Summering')
        sheet.getRow(8).getCell(1).setCellValue('Rapporterade timmar')
        sheet.getRow(9).getCell(1).setCellValue('Produktionskapacitet')
        sheet.getRow(10).getCell(1).setCellValue('Produktionstimmar')

        sheet.getRow(11).getCell(0).setCellValue('EOn')

        sheet.getRow(19).getCell(0).setCellValue('Annan FindOut tid')
        sheet.getRow(20).getCell(1).setCellValue('Varav OH')
        sheet.getRow(21).getCell(1).setCellValue('Headcounts på OH')
        sheet.getRow(22).getCell(1).setCellValue('Varav Kompetensutveckling')

        sheet.getRow(23).getCell(0).setCellValue('Annan tid')
        sheet.getRow(24).getCell(1).setCellValue('Varav Sjukdom, VAB, etc')
        sheet.getRow(25).getCell(1).setCellValue('Varav Semester')
        sheet.getRow(26).getCell(1).setCellValue('Varav Föräldrarledighet ')

        List timereportMonths = TimeReportMonth.withCriteria {
            between('date', startOfFiscalYear.toDate(), startOfFiscalYear.plusYears(1).minusDays(1).toDate())
        }.sort {
            it.date
        }

        double totalBudget = MonthlyReport.withCriteria {
            between('date', startOfFiscalYear.toDate(), startOfFiscalYear.plusYears(1).minusDays(1).toDate())

            projections {
                sum('standardTime')
            }
        }[0] ?: 0

        List totalWorkdayActivities = ActivityReport.withCriteria {
            between('date', startOfFiscalYear.toDate(), startOfFiscalYear.plusYears(1).minusDays(1).toDate())

            projections {
                groupProperty('activity')
                sum('hours')
            }
        }

        Map totalOfferAreas = exportService.getOfferAreas(totalWorkdayActivities)
        double totalProductionHours = totalOfferAreas.findAll{it.key != 'Not Specified'}*.value.sum() ?: 0
        double totalReportedHours = totalWorkdayActivities*.getAt(1).sum() ?: 0
        double totalVacation = totalWorkdayActivities.find{it[0].name == 'Semester'}?.getAt(1) ?: 0
        double totalParentalLeave = totalWorkdayActivities.findAll{it[0].name.contains('Föräldrarledig')}*.getAt(1).sum() ?: 0
        double totalSickness =  totalWorkdayActivities.find{it[0].name == 'Sjukdom'}?.getAt(1) ?:0
        double totalVab = totalWorkdayActivities.find{it[0].name == 'VAB'}?.getAt(1)?:0
        double totalSkillsDevelopment = totalWorkdayActivities.find{it[0].name == 'Kompetensutveckling'}?.getAt(1)?:0




        // gets monthly data for offerAreas and activities
        12.times { monthIndex ->
            DateTime iteratingMonth = startOfFiscalYear.plusMonths(monthIndex)
            TimeReportMonth reportMonth = timereportMonths[monthIndex]

            List activityReportActivities = ActivityReport.withCriteria {
                between('date', iteratingMonth.toDate(), iteratingMonth.plusMonths(1).minusDays(1).toDate())

                projections {
                    groupProperty('activity')
                    sum('hours')
                }
            }


            double oh = activityReportActivities.find{it[0].name == 'OH (används endast av OH personal)'}?.getAt(1) ?: 0
            double vacation = activityReportActivities.find{it[0].name == 'Semester'}?.getAt(1) ?: 0
            double parentalLeave = activityReportActivities.findAll{it[0].name.contains('Föräldrarledig')}*.getAt(1).sum() ?: 0
            double sickness =  activityReportActivities.find{it[0].name == 'Sjukdom'}?.getAt(1) ?:0
            double vab = activityReportActivities.find{it[0].name == 'VAB'}?.getAt(1)?:0
            double skillsDevelopment = activityReportActivities.find{it[0].name == 'Kompetensutveckling'}?.getAt(1)?:0

            double budget = MonthlyReport.withCriteria {
                between('date', iteratingMonth.toDate(), iteratingMonth.plusMonths(1).minusDays(1).toDate())

                projections {
                    sum('standardTime')
                }
            }[0] ?: 0

            double reportedHours = activityReportActivities*.getAt(1).sum() ?: 0

            double reportedHoursBudgetDiff = reportedHours - budget

            Map offerAreas = exportService.getOfferAreas(activityReportActivities)
            List nonProductionOfferAreas = [
                    'Other FindOut time',
                    'Other time'
            ]
            double productionHours = offerAreas.findAll{!(it.key in nonProductionOfferAreas)}*.value?.sum() ?: 0
            double otherFindOutTime = offerAreas.findAll{it.key == 'Other FindOut time'}*.value?.sum() ?: 0


            // write monthly data to excel

            sheet.getRow(2).getCell(3+monthIndex).setCellValue(iteratingMonth.monthOfYear().asShortText)
            sheet.getRow(3).getCell(3+monthIndex).setCellValue(reportMonth?.standardTime)
            sheet.getRow(4).getCell(3+monthIndex).setCellValue(budget)
            sheet.getRow(5).getCell(3+monthIndex).setCellValue(reportedHours)
            sheet.getRow(6).getCell(3+monthIndex).setCellValue(reportedHoursBudgetDiff)

            sheet.getRow(8).getCell(3+monthIndex).setCellValue(reportedHours)

            sheet.getRow(10).getCell(3+monthIndex).setCellValue(productionHours)

            offerAreaMapping.eachWithIndex{ offerArea, index ->
                sheet.getRow(12+index).getCell(1).setCellValue(offerArea.value)
                sheet.getRow(12+index).getCell(3+monthIndex).setCellValue(offerAreas[offerArea.key] ?: 0)
            }

            sheet.getRow(19).getCell(3+monthIndex).setCellValue(otherFindOutTime)
            sheet.getRow(20).getCell(3+monthIndex).setCellValue(oh)
            sheet.getRow(22).getCell(3+monthIndex).setCellValue(skillsDevelopment)

            sheet.getRow(23).getCell(3+monthIndex).setCellValue(sickness + vab + vacation + parentalLeave)
            sheet.getRow(24).getCell(3+monthIndex).setCellValue(sickness + vab)
            sheet.getRow(25).getCell(3+monthIndex).setCellValue(vacation)
            sheet.getRow(26).getCell(3+monthIndex).setCellValue(parentalLeave)

        }

        // Write accumulated data to excel
        sheet.getRow(4).getCell(2).setCellValue(totalBudget)
        sheet.getRow(5).getCell(2).setCellValue(totalReportedHours)

        sheet.getRow(8).getCell(2).setCellValue(totalReportedHours)

        sheet.getRow(10).getCell(2).setCellValue(totalProductionHours)

        offerAreaMapping.eachWithIndex{ offerArea, index ->
            sheet.getRow(12+index).getCell(2).setCellValue(totalOfferAreas[offerArea.key] ?: 0)
        }

        sheet.getRow(22).getCell(2).setCellValue(totalSkillsDevelopment)

        sheet.getRow(23).getCell(2).setCellValue(totalSickness + totalVab + totalVacation + totalParentalLeave)
        sheet.getRow(24).getCell(2).setCellValue(totalSickness + totalVab)
        sheet.getRow(25).getCell(2).setCellValue(totalVacation)
        sheet.getRow(26).getCell(2).setCellValue(totalParentalLeave)


        new File()

        response.setHeader("Content-disposition", "attachment; filename=\"input till lonsamhetsmodell.xlsx\"")
        response.setHeader("Content-Transfer-Encoding", 'binary')
//        response.contentType = 'application/excel'


        excelFile.properties.each{println it}


        render(contentType: "application/excel", text: excelFile);
//        excelFile.write(response.outputStream)
//
//        response.outputStream.flush()
//        response.outputStream.close()
    }
}
