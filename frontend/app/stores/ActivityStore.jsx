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
    setActivities: function(users){
        this.users = _.sortBy(users, function(user){ // sort activities by name
            return user.name;
        });

        this.trigger(this.users);
    },
    fetchActivityData: function () {
        DBService.get('/activities.json?&max=-1', this.setActivities);
    }
});
