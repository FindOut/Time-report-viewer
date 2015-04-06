var AppConfig = require('../AppConfig');
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
            return workday.activity.id
        }));

        // Get user IDs from workdays
        this.users = _.uniq(this.workdays.map(function(workday){
            return workday.user.id
        }));
    },

    setCurrentActivities: function(){
        var activities = [];
    },

    fetchWorkdays: function(filterData){
        console.log('doing login');
        $.ajax({
            url: AppConfig.serverURL + '/login',
            crossDomain: true,
            //headers: {
            //    authorization : "Basic"
            //},
            type: 'get',
            dataType: 'json',
            //xhrFields: {
            //    'origin': '*'
            //},
            //beforeSend: function (xhr) {
                //xhr.setRequestHeader(
                //    'Authorization',
                //    'Basic ' + btoa('test:test'));
            //}
        }).then(function(data){
            console.log('test');
            console.log(data);
        }.bind(this));

        //$.ajax({
        //    url: AppConfig.serverURL + '/workdays.json?&max=-1',
        //    crossDomain: true,
        //    data: filterData
        //}).then(function(data){
        //    this.workdays = data;
        //    this.setMetaProperties();
        //    this.trigger(this.workdays);
        //}.bind(this));
    }
});