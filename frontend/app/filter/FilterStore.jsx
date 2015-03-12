var Reflux = require('reflux');

module.exports = Reflux.createStore({
    filterConfiguration: [],
    filteredValues: {},

    setFilterConfiguration: function(filterData){
        this.filterConfiguration = filterData;

        this.initDefaultValues();
        this.initStoreListeners();
    },

    initDefaultValues: function(){
        this.filterConfiguration.forEach(function(property){
            switch(property.type) {
                case 'select':
                    this.filteredValues[property.serverProperty] = [];
                    break;
                default:
                    this.filteredValues[property.serverProperty] = '';
            }
        }.bind(this));
    },

    initStoreListeners: function(){
        this.filterConfiguration.forEach(function(property){
            if(property.dataStore !== undefined){
                property.dataStore.listen(function(activities){
                    property.data = activities;
                    //Trigger store update after filtervalues changed
                    this.trigger(this.filteredValues);
                }.bind(this));
            }
        }.bind(this))
    },



    setFilteredValue: function(property, data){
        // Values needs to be stringified to handle arrays
        if(JSON.stringify(data) !== JSON.stringify(this.filteredValues[property])){
            this.filteredValues[property] = data;

            console.log('saving');
            this.trigger(this.filteredValues);
        }
    },

    getFilteredValue: function(property){
        return this.filteredValues[property];
    }
});