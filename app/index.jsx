'use strict';

var React = require('react');
var Reflux = require('reflux');


require('../app/index.scss');

var ActivityStore = require('../app/stores/ActivityStore');
var Filter = require('../app/filter/Filter.jsx');
var WorkdayStore = require('../app/stores/WorkdayStore');

var monthNames = [ "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December" ];

var App = React.createClass({
    activities: [],
    filters: {},
    getInitialState: function(){
        return {
            activities: [],
            workdays: [],
            filters: {}
        };
    },
    componentDidMount: function(){
        ActivityStore.listen(this.activitiesUptadetd);
        WorkdayStore.listen(this.workdaysUpdated)
    },
    workdaysUpdated: function(workdays){
        this.setState({
            workdays: workdays
        });
    },
    activitiesUptadetd: function(activities){
        this.setState({
            activities: activities
        });
    },

    onFilterChange: function(filterData){
        WorkdayStore.fetchWorkdays(filterData);
    },

    render : function(){
        var filterProperties = [
            {
                label: 'Activities',
                type: 'select',
                serverProperty: 'activities',
                multiple: true,
                data: this.state.activities
            },
            {
                label: 'From',
                serverProperty: 'from',
                type: 'date'
            },
            {
                label: 'To',
                serverProperty: 'to',
                type: 'date'
            }
        ];

        var months = {};
        this.state.workdays.forEach(function(workday){
            var workdayMonth = monthNames[new Date (workday.date).getMonth()];
            months[workdayMonth] = (months[workdayMonth] || 0) + workday.hours;
        });

        var workdays = Object.keys(months).map(function(month){
            return (
                <li>{month}: {months[month]}</li>
            );
        });

        return (
            <div>
                <Filter filters={filterProperties} onChange={this.onFilterChange}/>
                <ul>{workdays}</ul>
            </div>
        )
    }
});

React.render(<App/>, document.body);
