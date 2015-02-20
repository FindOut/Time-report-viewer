var Reflux = require('reflux');

module.exports = Reflux.createStore({
    init: function(){
        this.fetchServerData(this.processData);
    },

    fetchServerData: function(callback){
        var activitiesLoaded = false,
            workdaysLoaded = false,
            activities = [],
            workdays = [];

        $.ajax({
            url: 'http://ceras.se/report/activities.json',
            crossDomain: true
        }).then(function (activitiesData) {
            activitiesLoaded = true;
            activities = activitiesData;
            if(activitiesLoaded && workdaysLoaded){
                callback({activities:activities, workdays: workdays});
            }
        }.bind(this));

        $.ajax({
            url: "http://ceras.se/report/workday.json",
            crossDomain: true
        }).then(function(workdaysData){
            workdaysLoaded = true;
            workdays = workdaysData;
            if(activitiesLoaded && workdaysLoaded){
                callback({activities:activities, workdays: workdays});
            }
        }.bind(this));
    },
    processData: function(data){
        console.log(data)
    }
});