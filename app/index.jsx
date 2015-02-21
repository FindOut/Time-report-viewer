'use strict';

var React = require('react');
var Reflux = require('reflux');


require('../app/index.scss');

var ActivityStore = require('../app/stores/ActivityStore');
var WorkdayStore = require('../app/stores/WorkdayStore');

var Filter = require('../app/filter/Filter.jsx');
var WorkdayViwer = require('../app/workdayViewer/workdayViewer');

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
        ActivityStore.listen(this.activitiesUpdated);
        WorkdayStore.listen(this.workdaysUpdated)
    },
    workdaysUpdated: function(workdays){
        this.setState({
            workdays: workdays
        });
    },
    activitiesUpdated: function(activities){
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
                label: 'From',
                serverProperty: 'from',
                type: 'date'
            },
            {
                label: 'To',
                serverProperty: 'to',
                type: 'date'
            },
            {
                label: 'Activities',
                type: 'select',
                serverProperty: 'activities',
                multiple: true,
                data: this.state.activities
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

        //<ul>{workdays}</ul>


        return (
            <div id="pageContainer">
                <Filter filters={filterProperties} onChange={this.onFilterChange}/>
                <WorkdayViwer/>
            </div>
        )
    }
});

React.render(<App/>, document.body);
