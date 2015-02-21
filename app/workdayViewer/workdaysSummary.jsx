var React = require('react'),
    WorkdayStore = require('../stores/WorkdayStore');

var monthNames = [ "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December" ];

module.exports = React.createClass({
    workdaysUpdated: function(workdays){
        this.setState({
            workdays: workdays,
            currentActivities: WorkdayStore.getCurrentActivities()
        });
    },


    getInitialState: function () {
        return {
            currentActivities: [],
            workdays: []
        }
    },

    componentDidMount: function () {
        WorkdayStore.listen(this.workdaysUpdated);
    },

    render: function () {
        var months = {};
        //console.log(this.state.workdays);
        if(this.state.workdays !== undefined){
            this.state.workdays.forEach(function(workday){
                var workdayMonth = monthNames[new Date (workday.date).getMonth()];
                months[workdayMonth] = (months[workdayMonth] || 0) + workday.hours;
            });
        }

        //console.log(months);

        var workdays = Object.keys(months).map(function(month){
            return (
                <li>{month}: {months[month]}</li>
            );
        });

        return (
            <div id="workdaysSummary">
                <ul>
                    {workdays}
                </ul>
            </div>
        );
    }
});