var React = require('react');

var monthNames = [ "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December" ];

module.exports = React.createClass({
    render: function () {

        var months = {};
        if(this.props.workdays !== undefined){
            console.log(this.props);
            this.props.workdays.forEach(function(workday){
                var workdayMonth = monthNames[new Date (workday.date).getMonth()];
                months[workdayMonth] = (months[workdayMonth] || 0) + workday.hours;
            });
        }


        var workdays = Object.keys(months).map(function(month){
            return (
                <li>{month}: {months[month]}</li>
            );
        });

        //<ul>{workdays}</ul>

        return (
            <div id="workdaysSummary">
                <ul>
                    {workdays}
                </ul>
            </div>
        );
    }
});