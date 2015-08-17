var Reflux = require('reflux');

module.exports = Reflux.createStore({
    filteredValues: {},

    setFilteredValue: function(property, data){
        // Values needs to be stringified to handle arrays
        if(JSON.stringify(data) !== JSON.stringify(this.filteredValues[property])){
            this.filteredValues[property] = data;
            this.trigger(this.filteredValues);
        }
    },

    getFilteredValue: function(property){
        return this.filteredValues[property];
    }
});