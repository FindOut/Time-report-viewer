var AppConfig = require('../AppConfig'),
    Reflux = require('reflux');

module.exports = Reflux.createStore({
    users: [],

    getUsers: function(){
        return this.users;
    },
    fetchUsers: function () {
        //$.ajax({
        //        url: AppConfig.serverURL + '/users.json?&max=-1',
        //        crossDomain: true
        //    }).then(function(users){
        //    this.users = _.sortBy(users, function(user){ // sort activities by name
        //        return user.name;
        //    });
        //
        //    this.trigger(this.users);
        //}.bind(this));
    },

    init: function(){
        this.fetchUsers();
    }
});