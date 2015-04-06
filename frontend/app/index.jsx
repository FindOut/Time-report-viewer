'use strict';
var React = require('react');
var Reflux = require('reflux');
var AppConfig = require('./AppConfig');

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
        console.log('doing login');
        $.ajax({
            url: AppConfig.serverURL + '/login',
            //crossDomain: true,
            type: 'POST',
            dataType: 'json',
            contentType: "application/json; charset=UTF-8",
            data: JSON.stringify({
                username: 'test',
                password: 'test'
            })
            //beforeSend: function (xhr) {
            //xhr.setRequestHeader(
            //    'authorization',
            //    'Basic ' + btoa('test:test'));
            //}
        }).then(function(data){
            console.log('test');
            console.log(data);
        }.bind(this));

        return (
            <div id="pageContainer">
                <Filter/>
                <WorkdayViewer/>
            </div>
        )
    }
});

React.render(<App/>, document.body);