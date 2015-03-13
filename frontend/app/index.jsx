'use strict';
var React = require('react');
var Reflux = require('reflux');

require('../app/index.scss');

var ActivityStore = require('../app/stores/ActivityStore');
var UserStore = require('../app/stores/UserStore');
var WorkdayStore = require('../app/stores/WorkdayStore');
var FilterStore = require('./filter/FilterStore');

var Filter = require('../app/filter/Filter.jsx');
var WorkdayViewer = require('../app/workdayViewer/workdayViewer');

var App = React.createClass({
    componentWillMount: function(){
        var filterProperties = [
            {
                label: 'From',
                serverProperty: 'from',
                type: 'date'
            }, {
                label: 'To',
                serverProperty: 'to',
                type: 'date'
            }, {
                label: 'Activities',
                type: 'select',
                serverProperty: 'activities',
                multiple: true,
                dataStore: ActivityStore
            }, {
                label: 'Users',
                type: 'select',
                serverProperty: 'users',
                multiple: true,
                dataStore: UserStore
            }
        ];

        FilterStore.setFilterConfiguration(filterProperties);
        FilterStore.listen(WorkdayStore.fetchWorkdays);
    },

    render : function(){
        return (
            <div id="pageContainer">
                <Filter/>
                <WorkdayViewer/>
            </div>
        )
    }
});

React.render(<App/>, document.body);