var AppConfig = require('../AppConfig');
var DBService = require('../DBService');
var Reflux = require('reflux');
var _ = require('lodash');

module.exports = Reflux.createStore({
    workdays: [],

    currentActivities: [],
    currentUsers: [],

    getWorkdays: function(){
        return this.workdays;
    },

    getCurrentActivities: function(){
        return this.activities;
    },

    getCurrentUsers: function(){
        return this.users;
    },

    setMetaProperties: function(){
        // Get activity IDs from workdays
        this.activities = _.uniq(this.workdays.map(function(workday){
            return workday.activity
        }), 'id');

        // Get user IDs from workdays
        this.users = _.uniq(this.workdays.map(function(workday){
            return workday.user
        }), 'id');
    },

    setWorkdays: function(data){
        this.workdays = data;
        this.setMetaProperties();
        this.trigger(this.workdays);
    },

    fetchWorkdays: function(filterData){
        DBService.get('/workdays.json?&max=-1', this.setWorkdays, filterData);
    }
});