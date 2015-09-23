'use strict';
var React = require('react');
var Reflux = require('reflux');
var LoginStore = require('../source/stores/LoginStore');

require('../source/index.scss');

var ActivityStore = require('../source/stores/ActivityStore');
var OfferAreaStore = require('../source/stores/OfferAreaStore');
var EmployeeStore = require('../source/stores/EmployeeStore');
var ActivityReportStore = require('../source/stores/ActivityReportStore');
var FilterStore = require('./filter/FilterStore');

var Menu = require('../source/menu/menuButton.jsx');
var Filter = require('../source/filter/Filter.jsx');
var ActivityReportViewer = require('../source/activityReportViewer/activityReportViewer');
var Login = require('../source/Login');

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

        var menuConfiguration = {
            items: [
                {text: 'Import data from dropbox', url: '/import/importDataFromDropbox'},
                {text: 'Export profitability basis', url: "/export/profitabilityBasis"}
            ]
        };


        if(!LoginStore.isAuthorized()){
            return (<Login></Login>);
        } else {
            return (
                <div id="pageContainer">
                    <Menu {...menuConfiguration}/>
                    <Filter filterConfiguration={filterConfiguration}/>
                    <ActivityReportViewer/>
                </div>
            );
        }
    }
});

React.render(<App/>, document.body);