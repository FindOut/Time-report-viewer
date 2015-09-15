var React = require('react'),
    Highcharts = require('react-highcharts'),
    _ = require('lodash'),
    moment = require('moment');

module.exports = React.createClass({
    getSummarizedActivityReports_days: function () {
        var summarizedActivityReports_days = [];

        if(this.props.activityReports.length > 0){
            var dates = _.uniq(this.props.activityReports.map(function(activityReport){
                return activityReport.date;
            }));
            dates.sort();


            summarizedActivityReports_days = dates.map(function(date){
                var activityReportsInDate = _.filter(this.props.activityReports, function(activityReport){
                    return activityReport.date === date;
                });
                var activityReportsHoursInDate = activityReportsInDate.map(function(activityReport){
                    return activityReport.hours;
                }).reduce(function(total, currentValue){
                    return total + currentValue
                });

                return [
                    new Date(date).getTime(),
                    activityReportsHoursInDate
                ]
            }.bind(this));
        }
        return summarizedActivityReports_days
    },

    getSummarizedActivityReports_weeks: function () {
        var summarizedActivityReports_weeks = [];

        if(this.props.activityReports.length > 0){
            var data = {};

            this.props.activityReports.forEach(function(activityReport){
                var week = moment(activityReport.date).weekday(0).toDate().getTime();

                if(!data[week]){
                    data[week] = activityReport.hours;
                } else {
                    data[week] += activityReport.hours
                }
            });

            var weeks = Object.keys(data);
            weeks.sort();

            summarizedActivityReports_weeks = weeks.map(function(week){
                if(data[week] !== undefined){
                    return [
                        parseInt(week, 10),
                        data[week]
                    ]
                } else {
                    return [
                        parseInt(week, 10),
                        null
                    ]
                }
            }.bind(this));
        }
        return summarizedActivityReports_weeks
    },

    render: function() {
        var start = new Date().getTime();
        var summarizedActivityReports_days = this.getSummarizedActivityReports_weeks();

        var config = {
            chart: {
                type: 'column',
                backgroundColor:'transparent',
                spacingLeft: 0
            },
            title: {
                text: 'Time in selection',
                x: -20 //center
            },
            xAxis: {
                type: 'datetime',
                dateTimeLabelFormats: { // don't display the dummy year
                    month: '%e. %b',
                    year: '%b'
                }
            },
            yAxis: {
                title: {
                    text: 'Hours'
                },
                gridLineColor: '#CCCCCC'
            },
            tooltip: {
                pointFormat: '{point.x:%e. %b}: {point.y:.2f}h'
            },
            legend: {
                enabled: false
            },
            credits: {
                enabled: false
            },
            plotOptions: {
                series: {
                    animation: false
                }
            },
            series: [
                {
                    data: summarizedActivityReports_days
                }
            ]
        };

        return (
            <Highcharts config={config}></Highcharts>
        )
    }
});