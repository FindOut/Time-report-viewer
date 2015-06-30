var React = require('react');

module.exports = React.createClass({
    render: function() {
        var renderedActivities = this.props.activities.map(function (activity) {
            //var activityWorkdays = _.filter(this.props.workdays, function(workday){
            //    return workday.activity.id === activity.id;
            //});
            //var activityHours = activityWorkdays.map(function(workday){
            //    return workday.hours
            //}).reduce(function(total, currentValue){
            //    return total + currentValue
            //});
            //activityHours = Math.round(activityHours*100)/100;

            return (
                <li>
                    {activity.name} | <i>{activity.offerArea.name}</i>
                </li>
            )
        }, this);

        return (
            <div id="activitiesInSelection">
                <h4>Activities</h4>
                <ul>
                    {renderedActivities}
                </ul>
            </div>
        )
    }
});