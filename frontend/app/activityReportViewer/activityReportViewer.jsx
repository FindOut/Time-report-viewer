var React = require('react'),
    ActivityReportStore = require('../stores/ActivityReportStore'),
    ActivityStore = require('../stores/ActivityStore'),
    EmployeeStore = require('../stores/EmployeeStore'),
    ActivityReportsSummary = require('./activityReportSummary'),
    EmployeesInActivityReportsSelection = require('./employeesInActivityReportsSelection'),
    ActivitiesInActivityReportsSelection = require('./activitiesInActivityReportsSelection'),
    ActivityReportSelectionMetaData = require('./activityReportSelectionMetaData'),
    ActivityReportSelectionDetails = require('./activityReportSelectionDetails');

module.exports = React.createClass({
    getInitialState: function(){
        return {
            activityReports: [],
            currentActivities: [],
            currentEmployees: []
        };
    },

    activityReportsUpdated: function(activityReports){
        var currentActivityIds = ActivityReportStore.getCurrentActivities().map(function(activity){return activity.id});
        var currentActivities = _.filter(ActivityStore.getActivities(), function(activity){
            return _.contains(currentActivityIds, activity.id);
        });

        var currentEmployeeIds = ActivityReportStore.getCurrentEmployees().map(function(employee){return employee.id});
        var currentEmployees = _.filter(EmployeeStore.getEmployees(), function(employee){
            return _.contains(currentEmployeeIds, employee.id);
        });
        this.setState({
            activityReports: activityReports,
            currentActivities: currentActivities,
            currentEmployees: currentEmployees
        })
    },

    componentDidMount: function(){
        ActivityReportStore.listen(this.activityReportsUpdated);
    },

//<ActivityReportsSummary activityReports={this.state.activityReports} />
//<EmployeesInActivityReportsSelection employees={this.state.currentEmployees}/>
//<ActivityReportsDetails
//    activityReports={this.state.activityReports}
//    activities={this.state.currentActivities}/>
    render: function(){
        return (
            <div id="activityReportViewer">
                <div id="upper">
                    <ActivityReportSelectionMetaData activityReports={this.state.activityReports} />
                    <ActivitiesInActivityReportsSelection activities={this.state.currentActivities}/>
                    <EmployeesInActivityReportsSelection employees={this.state.currentEmployees}/>
                </div>
                <div id="lower">
                    <ActivityReportSelectionDetails activityReports={this.state.activityReports} />
                </div>
            </div>
        );
    }
});