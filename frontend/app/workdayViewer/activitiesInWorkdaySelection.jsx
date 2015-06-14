var React = require('react');

module.exports = React.createClass({
    render: function() {
        var renderedActivities = this.props.activities.map(function (activity) {
            return (
                <li>
                    {activity.name} | <i>{activity.offerArea.name}</i>
                </li>
            )
        });

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