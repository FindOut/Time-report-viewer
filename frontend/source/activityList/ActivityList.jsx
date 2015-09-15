var React = require('react');
var ActivityStore = require('../activityList/ActivityStore');
var _ = require('lodash');
//var ActivityReportsStore = require('../activityReports/ActivityReportsStore');
require('../activityList/activityList.scss');

module.exports = React.createClass({
    getInitialState: function(){
        return {
            activities: [],
            activityReports: []
        };
    },
    onStoreChange: function(activityData){
        this.setState({
            activities: activityData.activities,
            activityReports: activityData.activityReports
        });
    },
    componentDidMount: function(){
        this.unsubscribe = ActivityStore.listen(this.onStoreChange);
//        this.unsubscribe = ActivityReportsStore.listen(this.activityReportsChanged);
    },
    componentWillUnmount: function() {
        this.unsubscribe();
    },

    getActivityReports: function(activityId){
        return _.filter(this.state.activityReports, function(activityReport){
            return activityReport.activity.id === activityId;
        });
    },
    render: function(){
        var renderedActivities = this.state.activities.map(function(activity){
            var activityActivityReports = this.getActivityReports(activity.id);
            var activityHours = activityActivityReports.reduce(function(total, activityReport){
                return total + parseFloat(activityReport.hours, 10);
            },0);
            return (<li>{activity.id}: {activity.name}<span className="hours">{activityHours}</span></li>)
        }.bind(this));

        return(
            <ul id="activityList">{renderedActivities}</ul>
        );
    }
});