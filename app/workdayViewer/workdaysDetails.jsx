var React = require('react');

module.exports = React.createClass({
    render: function(){
        //console.log(this.props.activities);
        var renderedActivities = this.props.activities.map(function(activity){
            var activityWorkdays = _.filter(this.props.workdays, function(workday){
                return workday.activity.id === activity.id;
            });
            return (
                <div>
                    {activity.name}
                </div>
            )
        }, this);

        console.log(renderedActivities);

        return (
            <div id="workdaysDetails">
                {renderedActivities}
            </div>
        )
    }
});