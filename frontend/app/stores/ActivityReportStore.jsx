var AppConfig = require('../AppConfig');
var DBService = require('../DBService');
var Reflux = require('reflux');
var _ = require('lodash');

module.exports = Reflux.createStore({
    activityReports: [],

    currentActivities: [],
    currentEmployees: [],

    getActivityReports: function(){
        return this.activityReports;
    },

    getCurrentActivities: function(){
        return this.activities;
    },

    getCurrentEmployees: function(){
        return this.currentEmployees;
    },

    setMetaProperties: function(){
        // Get activity IDs from activityReports
        this.activities = _.uniq(this.activityReports.map(function(activityReport){
            return activityReport.activity
        }), 'id');

        // Get employee IDs from activityReports
        this.currentEmployees = _.uniq(this.activityReports.map(function(activityReport){
            return activityReport.employee
        }), 'id');
    },

    setActivityReports: function(data){
        this.activityReports = data;
        this.setMetaProperties();
        this.trigger(this.activityReports);
    },

    fetchActivityReports: function(filterData){
        DBService.get('/activityReports.json?&max=-1', this.setActivityReports, filterData);
    }
});