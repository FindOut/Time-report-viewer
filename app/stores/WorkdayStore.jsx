var Reflux = require('reflux');
var _ = require('lodash');

module.exports = Reflux.createStore({
    workdays: [],

    getWorkdays: function(){
        return this.workdays;
    },

    getCurrentUsers: function(){
        var users = [];
        this.workdays.forEach(function(workday){
            if(!_.contains(users, workday.user.id)){
                users.push(workday.user.id)
            }
        });

        return users;
    },
    fetchWorkdays: function(filterData){
        $.ajax({
            url: "http://ceras.se/report/workdays.json?&max=-1",
            crossDomain: true,
            data: filterData
        }).then(function(data){
            this.workdays = data;
            this.trigger(this.workdays);
        }.bind(this));
    }
});