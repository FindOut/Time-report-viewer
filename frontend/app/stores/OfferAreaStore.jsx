var DBService = require('../DBService'),
    Reflux = require('reflux'),
    _ = require('lodash'),
    LoginStore = require('./LoginStore');

module.exports = Reflux.createStore({
    offerAreas: [],

    init: function () {
        LoginStore.listen(this.fetchOfferAreaData);
    },
    getOfferAreas: function(){
        return this.offerAreas;
    },

    setOfferAreas: function(offerAreas){
        this.offerAreas = _.sortBy(offerAreas, function(offerArea){ // sort offerAreas by name
            return offerArea.name;
        });

        this.trigger(this.offerAreas);
    },
    fetchOfferAreaData: function () {
        DBService.get('/offerAreas.json?&max=-1', this.setOfferAreas);
    }
});
