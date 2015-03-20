var AppConfig = require('../AppConfig'),
    Reflux = require('reflux'),
    _ = require('lodash');

module.exports = Reflux.createStore({
    activities: [],

    init: function () {
        this.fetchActivityData();
    },
    getActivities: function(){
        return this.activities;
    },
    fetchActivityData: function () {
        $.ajax({
            url: AppConfig.serverURL + '/activities.json?max=-1',
            crossDomain: true
        }).then(function (activities) {
            this.activities = _.sortBy(activities, function(activity){ // sort activities by name
                return activity.name;
            });
            this.trigger(this.activities);
        }.bind(this));
    }
});
