var React = require('react');

module.exports = React.createClass({
    render: function() {
        var renderedActivities = this.props.activities.map(function (activity) {
            //var activityActivityReports = _.filter(this.props.activityReports, function(activityReport){
            //    return activityReport.activity.id === activity.id;
            //});
            //var activityHours = activityActivityReports.map(function(activityReport){
            //    return activityReport.hours
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