'use strict';

var React = require('react');
var Reflux = require('reflux');


require('../app/index.scss');

var ActivityStore = require('../app/stores/ActivityStore');
var UserStore = require('../app/stores/UserStore');
var WorkdayStore = require('../app/stores/WorkdayStore');

var Filter = require('../app/filter/Filter.jsx');
var WorkdayViewer = require('../app/workdayViewer/workdayViewer');

var App = React.createClass({
    activities: [],
    filters: {},
    getInitialState: function(){
        return {
            activities: [],
            workdays: [],
            users: [],
            filters: {}
        };
    },
    componentDidMount: function(){
        ActivityStore.listen(this.activitiesUpdated);
        UserStore.listen(this.usersUpdated);
        WorkdayStore.listen(this.workdaysUpdated)
    },

    activitiesUpdated: function(activities){
        this.setState({
            activities: activities
        });
    },
    usersUpdated: function(users){
        this.setState({
            users: users
        });
    },
    workdaysUpdated: function(workdays){
        this.setState({
            workdays: workdays
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
            },
            {
                label: 'Users',
                type: 'select',
                serverProperty: 'users',
                multiple: true,
                data: this.state.users
            }
        ];

        return (
            <div id="pageContainer">
                <Filter filters={filterProperties} onChange={this.onFilterChange}/>
                <WorkdayViewer/>
            </div>
        )
    }
});

React.render(<App/>, document.body);
