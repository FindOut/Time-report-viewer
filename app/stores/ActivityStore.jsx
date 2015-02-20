var Reflux = require('reflux');

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
            url: 'http://ceras.se/report/activities.json?max=-1',
            crossDomain: true
        }).then(function (activities) {
            this.trigger(activities);
        }.bind(this));
    }
});
