var DBService = require('../DBService'),
    Reflux = require('reflux'),
    _ = require('lodash'),
    LoginStore = require('./LoginStore');

module.exports = Reflux.createStore({
    activities: [],

    init: function () {
        LoginStore.listen(this.fetchActivityData);
    },
    getActivities: function(){
        return this.activities;
    },
    setActivities: function(activities){
        this.activities = _.sortBy(activities, function(activity){ // sort activities by name
            return activity.name;
        });

        this.trigger(this.activities);
    },
    fetchActivityData: function () {
        DBService.get('/activities.json?&max=-1', this.setActivities);
    }
});
