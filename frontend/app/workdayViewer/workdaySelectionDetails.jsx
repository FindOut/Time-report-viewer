var React = require('react'),
    Highcharts = require('react-highcharts'),
    _ = require('lodash'),
    moment = require('moment');

module.exports = React.createClass({
    getChartConfig: function(){
        return {
            title: {
                text: 'Time in selection',
                x: -20 //center
            },
            xAxis: {
                categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
            },
            yAxis: {
                title: {
                    text: 'Hours'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                valueSuffix: '°C'
            },
            legend: {
                enabled: false
            },
            credits: {
                enabled: false
            },
            series: [
                {
                    data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
                }
            ]
        };
    },

    getSummarizedWorkdays_days: function () {
        var summarizedWorkdays_days = [];

        if(this.props.workdays.length > 0){
            var dates = _.uniq(this.props.workdays.map(function(workday){
                return workday.date;
            }));
            dates.sort();


            summarizedWorkdays_days = dates.map(function(date){
                var workdaysInDate = _.filter(this.props.workdays, function(workday){
                    return workday.date === date;
                });
                var workdaysHoursInDate = workdaysInDate.map(function(workday){
                    return workday.hours;
                }).reduce(function(total, currentValue){
                    return total + currentValue
                });

                return [
                    new Date(date).getTime(),
                    workdaysHoursInDate
                ]
            }.bind(this));
        }
        return summarizedWorkdays_days
    },

    getSummarizedWorkdays_weeks: function () {
        var summarizedWorkdays_weeks = [];

        if(this.props.workdays.length > 0){
            var data = {};

            this.props.workdays.forEach(function(workday){
                var week = moment(workday.date).weekday(0).toDate().getTime();

                if(!data[week]){
                    data[week] = workday.hours;
                } else {
                    data[week] += workday.hours
                }
            });

            var weeks = Object.keys(data);
            weeks.sort();

            summarizedWorkdays_weeks = weeks.map(function(week){
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
        return summarizedWorkdays_weeks
    },

    render: function() {
        var start = new Date().getTime();
        var summarizedWorkdays_days = this.getSummarizedWorkdays_weeks();

        var config = {
            chart: {
                type: 'column'
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
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
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
            series: [
                {
                    data: summarizedWorkdays_days
                }
            ]
        };

        return (
            <Highcharts config={config}></Highcharts>
        )
    }
});