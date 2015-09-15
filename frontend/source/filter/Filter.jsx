var React = require('react');
var _ = require('lodash');
var FilterStore = require('./FilterStore');

require('./filter.scss');
var SelectFilter = require('./parts/SelectFilter');
var DateFilter = require('./parts/DateFilter');

module.exports = React.createClass({

    getInitialState: function() {
        return {
            filterData: {}
        };
    },

    createSelectFilter: function(filterProperty){
        filterProperty.data = filterProperty.dataAction !== undefined ? this.state[filterProperty.dataAction] : filterProperty.data;

        return (
            <SelectFilter
                ref={filterProperty.serverProperty}
                filterProperties={filterProperty}/>
        );
    },

    createDateFilter: function(filterProperty){
        return (
            <DateFilter
                ref={filterProperty.serverProperty}
                filterProperties={filterProperty}/>
        );
    },

    componentWillMount: function(){
        _.each(this.props.filterConfiguration, function(filterItem){
            if(filterItem.dataStore !== undefined && filterItem.dataAction !== undefined){
                this.setStateForFilterItemData(filterItem, {});

                filterItem.dataStore.listen(function(){
                    this.setStateForFilterItemData(filterItem, {});
                }, this);
            }
        }, this);

        FilterStore.trigger();// Trigger filtering
    },

    setStateForFilterItemData: function(filterItem, object){
        object[filterItem.dataAction] = filterItem.dataStore[filterItem.dataAction]();
        this.setState(object);
    },

    render: function(){
        var renderedFilters = this.props.filterConfiguration.map(function(filterProperty){
            switch (filterProperty.type) {
                case 'select':
                    return this.createSelectFilter(filterProperty);
                    break;
                case 'date':
                    return this.createDateFilter(filterProperty);
                    break;
            }
        }.bind(this));

        return (
            <div id="filter-container">
                <h3>Filters</h3>
                <form>
                    <ul>
                        {renderedFilters}
                    </ul>
                </form>
            </div>
        )
    }
});