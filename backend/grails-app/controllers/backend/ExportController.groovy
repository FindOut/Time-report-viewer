package backend

import grails.plugin.springsecurity.annotation.Secured
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import org.springframework.web.multipart.MultipartRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

class ExportController {

    def exportService

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def profitabilityDashboard(){
        MultipartRequest multipartRequest =  request as MultipartRequest
        CommonsMultipartFile file = multipartRequest.getFile('file')

        Workbook excelFile = new XSSFWorkbook(file.getInputStream())
        Sheet sheet = excelFile.getSheetAt(2)

        Map offerAreaMapping = [
                'D - Produktutveckling': ['Prod'],
                'D - Processutveckling': ['Proc'],
                'D - Verktygsutveckling': ['VU'],
                'I - Produktutveckling': ['Prod-I'],
                'I - Processutveckling': ['Proc-I'],
                'I - Verktygsutveckling': ['VU-?','VU-INV','VU-SPEK','VU-PINV'],
                'Ej specificerat': ['Not Specified']
        ]

        DateTime startOfFiscalYear = new DateTime().withTimeAtStartOfDay().withDayOfMonth(1).withMonthOfYear(5)

        12.times { monthIndex ->
            DateTime iteratingMonth = startOfFiscalYear.plusMonths(monthIndex)

            List activityReportActivities = ActivityReport.withCriteria {
                between('date', iteratingMonth.toDate(), iteratingMonth.plusMonths(1).minusDays(1).toDate())

                projections {
                    groupProperty('activity')
                    sum('hours')
                }
            }

            Map offerAreas = exportService.getOfferAreas(activityReportActivities)

            double oh = activityReportActivities.find{it[0].name == 'OH (används endast av OH personal)'}?.getAt(1) ?: 0
            double vacation = activityReportActivities.find{it[0].name == 'Semester'}?.getAt(1) ?: 0
            double parentalLeave = activityReportActivities.findAll{it[0].name.contains('Föräldrarledig')}*.getAt(1).sum() ?: 0
            double sickness =  activityReportActivities.find{it[0].name == 'Sjukdom'}?.getAt(1) ?:0
            double vab = activityReportActivities.find{it[0].name == 'VAB'}?.getAt(1)?:0

            double vuInv = offerAreas.findAll{ it.key in offerAreaMapping['I - Verktygsutveckling']}*.value.sum() ?: 0
            double vu = offerAreas.findAll{ it.key in offerAreaMapping['D - Verktygsutveckling']}*.value.sum() ?: 0
            double procInv = offerAreas.findAll{ it.key in offerAreaMapping['I - Processutveckling']}*.value.sum() ?: 0
            double proc = offerAreas.findAll{ it.key in offerAreaMapping['D - Processutveckling']}*.value.sum() ?: 0

            double reportedHours = activityReportActivities*.getAt(1).sum() ?: 0


            // write monthly data to excel

            sheet.getRow(23).getCell(3+monthIndex).setCellValue(reportedHours)
            sheet.getRow(24).getCell(3+monthIndex).setCellValue(parentalLeave)
            sheet.getRow(26).getCell(3+monthIndex).setCellValue(vacation)
            sheet.getRow(27).getCell(3+monthIndex).setCellValue(sickness + vab)

            sheet.getRow(96).getCell(3+monthIndex).setCellValue(oh)

            sheet.getRow(20).getCell(53+monthIndex).setCellValue(vuInv)
            sheet.getRow(22).getCell(53+monthIndex).setCellValue(vu)

            sheet.getRow(41).getCell(53+monthIndex).setCellValue(procInv)
            sheet.getRow(43).getCell(53+monthIndex).setCellValue(proc)

            sheet.getRow(65).getCell(53+monthIndex).setCellValue(procInv)
            sheet.getRow(67).getCell(53+monthIndex).setCellValue(proc)
        }

        response.setHeader("Content-disposition", /attachment; filename=lonsamhetsmodell.xlsx/)
        response.contentType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        excelFile.write(response.outputStream)
        response.outputStream.flush()
        response.outputStream.close()
    }
    def profitabilityBasis() {
        DateTime startOfFiscalYear = new DateTime().withTimeAtStartOfDay().withDayOfMonth(1).withMonthOfYear(5)

        Map offerAreaMapping = [
                'D - Produktutveckling': ['Prod'],
                'D - Processutveckling': ['Proc'],
                'D - Verktygsutveckling': ['VU'],
                'I - Produktutveckling': ['Prod-I'],
                'I - Processutveckling': ['Proc-I'],
                'I - Verktygsutveckling': ['VU-?','VU-INV','VU-SPEK','VU-PINV'],
                'Ej specificerat': ['Not Specified']
        ]

        List nonProductionOfferAreas = [
                'Other FindOut time',
                'Other time'
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

        double totalProductionHours = totalOfferAreas.findAll{!(it.key in nonProductionOfferAreas)}*.value.sum() ?: 0 // ska endast vara EO timmar
        double totalOtherFindOutTime = totalOfferAreas.find{it.key == 'Other FindOut time'}*.value?.sum()?:0

        double totalReportedHours = totalWorkdayActivities*.getAt(1).sum() ?: 0
        double totalVacation = totalWorkdayActivities.find{it[0].name == 'Semester'}?.getAt(1) ?: 0
        double totalParentalLeave = totalWorkdayActivities.findAll{it[0].name.contains('Föräldrarledig')}*.getAt(1).sum() ?: 0
        double totalSickness =  totalWorkdayActivities.find{it[0].name == 'Sjukdom'}?.getAt(1) ?:0
        double totalVab = totalWorkdayActivities.find{it[0].name == 'VAB'}?.getAt(1)?:0
        double totalSkillsDevelopment = totalWorkdayActivities.findAll{it[0].name.contains('Kompetensutveckling')}*.getAt(1).sum() ?:0
        double totalOH = totalWorkdayActivities.find{it[0].name == 'OH (används endast av OH personal)'}?.getAt(1)?:0

        double totalProductionCapacity = (totalOfferAreas.find{it.key != 'Other time'}*.value.sum() ?: 0) - totalOH


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

            Map offerAreas = exportService.getOfferAreas(activityReportActivities)

            double oh = activityReportActivities.find{it[0].name == 'OH (används endast av OH personal)'}?.getAt(1) ?: 0
            double vacation = activityReportActivities.find{it[0].name == 'Semester'}?.getAt(1) ?: 0
            double parentalLeave = activityReportActivities.findAll{it[0].name.contains('Föräldrarledig')}*.getAt(1).sum() ?: 0
            double sickness =  activityReportActivities.find{it[0].name == 'Sjukdom'}?.getAt(1) ?:0
            double vab = activityReportActivities.find{it[0].name == 'VAB'}?.getAt(1)?:0
            double skillsDevelopment = activityReportActivities.findAll{it[0].name.contains('Kompetensutveckling')}*.getAt(1).sum() ?:0

            double budget = MonthlyReport.withCriteria {
                between('date', iteratingMonth.toDate(), iteratingMonth.plusMonths(1).minusDays(1).toDate())

                projections {
                    sum('standardTime')
                }
            }[0] ?: 0

            double reportedHours = activityReportActivities*.getAt(1).sum() ?: 0

            double reportedHoursBudgetDiff = reportedHours - budget


            double productionHours = offerAreas.findAll{!(it.key in nonProductionOfferAreas)}*.value?.sum() ?: 0
            double productionCapacity = (offerAreas.findAll{it.key != 'Other time'}*.value?.sum() ?: 0) - oh
            double otherFindOutTime = offerAreas.findAll{it.key == 'Other FindOut time'}*.value?.sum() ?: 0


            // write monthly data to excel

            sheet.getRow(2).getCell(3+monthIndex).setCellValue(iteratingMonth.monthOfYear().asShortText)
            sheet.getRow(3).getCell(3+monthIndex).setCellValue(reportMonth?.standardTime)
            sheet.getRow(4).getCell(3+monthIndex).setCellValue(budget)
            sheet.getRow(5).getCell(3+monthIndex).setCellValue(reportedHours)
            sheet.getRow(6).getCell(3+monthIndex).setCellValue(reportedHoursBudgetDiff)

            sheet.getRow(8).getCell(3+monthIndex).setCellValue(reportedHours)

            sheet.getRow(9).getCell(3+monthIndex).setCellValue(productionCapacity)
            sheet.getRow(10).getCell(3+monthIndex).setCellValue(productionHours)

            offerAreaMapping.eachWithIndex{ offerArea, index ->
                double offerAreaSum = offerArea.value.inject(0){ double sum, String offerAreaName ->
                    sum += offerAreas[offerAreaName] ?: 0
                    sum
                }

                sheet.getRow(12+index).getCell(1).setCellValue(offerArea.key)
                sheet.getRow(12+index).getCell(3+monthIndex).setCellValue(offerAreaSum)
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
        sheet.getRow(9).getCell(2).setCellValue(totalProductionCapacity)
        sheet.getRow(10).getCell(2).setCellValue(totalProductionHours)

        offerAreaMapping.eachWithIndex{ offerArea, index ->
            double totalOfferAreaSum = offerArea.value.inject(0){ double sum, String offerAreaName ->
                sum += totalOfferAreas[offerAreaName] ?: 0
                sum
            }

            sheet.getRow(12+index).getCell(2).setCellValue(totalOfferAreaSum)
        }

        sheet.getRow(19).getCell(2).setCellValue(totalOtherFindOutTime)
        sheet.getRow(20).getCell(2).setCellValue(totalOH)
        sheet.getRow(22).getCell(2).setCellValue(totalSkillsDevelopment)

        sheet.getRow(23).getCell(2).setCellValue(totalSickness + totalVab + totalVacation + totalParentalLeave)
        sheet.getRow(24).getCell(2).setCellValue(totalSickness + totalVab)
        sheet.getRow(25).getCell(2).setCellValue(totalVacation)
        sheet.getRow(26).getCell(2).setCellValue(totalParentalLeave)

        response.setHeader("Content-disposition", /attachment; filename=input till lonsamhetsmodell.xlsx/)
        response.contentType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        excelFile.write(response.outputStream)
        response.outputStream.flush()
        response.outputStream.close()
    }
}
