var React = require('react'),
    ActivityReportStore = require('../stores/ActivityReportStore');

var monthNames = [ "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December" ];

module.exports = React.createClass({
    activityReportsUpdated: function(activityReports){
        this.setState({
            activityReports: activityReports,
            currentActivities: ActivityReportStore.getCurrentActivities()
        });
    },


    getInitialState: function () {
        return {
            currentActivities: [],
            activityReports: []
        }
    },

    componentDidMount: function () {
        ActivityReportStore.listen(this.activityReportsUpdated);
    },

    render: function () {
        var months = {};
        //console.log(this.state.activityReports);
        if(this.state.activityReports !== undefined){
            this.state.activityReports.forEach(function(activityReport){
                var activityReportMonth = monthNames[new Date (activityReport.date).getMonth()];
                months[activityReportMonth] = (months[activityReportMonth] || 0) + activityReport.hours;
                months[activityReportMonth] = Math.round(months[activityReportMonth] *100)/100;
            });
        }

        //console.log(months);

        var activityReports = [];
        monthNames.forEach(function(month){
            if(months[month] !== undefined){
                activityReports.push((
                    <li>{month.substr(0, 3)}<br/>
                        {months[month]}
                    </li>
                ));
            }
        });

        return (
            <div id="activityReportsSummary">
                <h3>Monthly summary</h3>
                <ul>
                    {activityReports}
                </ul>
            </div>
        );
    }
});