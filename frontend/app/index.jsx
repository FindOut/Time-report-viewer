'use strict';
var React = require('react');
var Reflux = require('reflux');
var LoginStore = require('../app/stores/LoginStore');

require('../app/index.scss');

var ActivityStore = require('../app/stores/ActivityStore');
var OfferAreaStore = require('../app/stores/OfferAreaStore');
var EmployeeStore = require('../app/stores/EmployeeStore');
var ActivityReportStore = require('../app/stores/ActivityReportStore');
var FilterStore = require('./filter/FilterStore');

var Filter = require('../app/filter/Filter.jsx');
var ActivityReportViewer = require('../app/activityReportViewer/activityReportViewer');
var Login = require('../app/Login');

var App = React.createClass({
    getInitialState: function () {
        return {
            loggedIn: false
        }
    },
    setLoggedIn: function(){ // this is to trigger re rendering when logged in
        var loggedIn = LoginStore.isAuthorized();

        if(this.loggedIn !== loggedIn){
            this.setState({
                loggedIn: loggedIn
            });
        }
    },
    componentWillMount: function(){
        FilterStore.listen(ActivityReportStore.fetchActivityReports);
        LoginStore.listen(this.setLoggedIn);
    },

    render : function(){
        var filterConfiguration = [
            {
                label: 'From',
                serverProperty: 'from',
                type: 'date'
            }, {
                label: 'To',
                serverProperty: 'to',
                type: 'date'
            }, {
                label: 'Offer areas',
                type: 'select',
                serverProperty: 'offerAreas',
                multiple: true,
                dataAction: 'getOfferAreas',
                dataStore: OfferAreaStore
            }, {
                label: 'Activities',
                type: 'select',
                serverProperty: 'activities',
                multiple: true,
                dataAction: 'getActivities',
                dataStore: ActivityStore
            }, {
                label: 'Employees',
                type: 'select',
                serverProperty: 'employees',
                multiple: true,
                dataAction: 'getEmployees',
                dataStore: EmployeeStore
            }
        ];

        if(!LoginStore.isAuthorized()){
            return (<Login></Login>);
        } else {
            return (
                <div id="pageContainer">
                    <Filter filterConfiguration={filterConfiguration}/>
                    <ActivityReportViewer/>
                </div>
            )
        }
    }
});

React.render(<App/>, document.body);