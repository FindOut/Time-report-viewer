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
            this.activities = activities.sort(function(a, b){ // sort activities by name
                if (a.name > b.name) { return 1; }
                if (a.name < b.name) { return -1; }
                return 0;
            });
            this.trigger(this.activities);
        }.bind(this));
    }
});
