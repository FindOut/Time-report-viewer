var React = require('react');
var ActivityStore = require('../activityList/ActivityStore');
var _ = require('lodash');
//var WorkdaysStore = require('../workdays/WorkdaysStore');
require('../activityList/activityList.scss');

module.exports = React.createClass({
    getInitialState: function(){
        return {
            activities: [],
            workdays: []
        };
    },
    onStoreChange: function(activityData){
        this.setState({
            activities: activityData.activities,
            workdays: activityData.workdays
        });
    },
    componentDidMount: function(){
        this.unsubscribe = ActivityStore.listen(this.onStoreChange);
//        this.unsubscribe = WorkdaysStore.listen(this.workdaysChanged);
    },
    componentWillUnmount: function() {
        this.unsubscribe();
    },

    getWorkdays: function(activityId){
        return _.filter(this.state.workdays, function(workday){
            return workday.activity.id === activityId;
        });
    },
    render: function(){
        var renderedActivities = this.state.activities.map(function(activity){
            var activityWorkdays = this.getWorkdays(activity.id);
            var activityHours = activityWorkdays.reduce(function(total, workday){
                return total + parseFloat(workday.hours, 10);
            },0);
            return (<li>{activity.id}: {activity.name}<span className="hours">{activityHours}</span></li>)
        }.bind(this));

        return(
            <ul id="activityList">{renderedActivities}</ul>
        );
    }
});