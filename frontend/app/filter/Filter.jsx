var React = require('react');
var _ = require('lodash');
var FilterStore = require('./FilterStore');

require('./filter.scss');
var SelectFilter = require('./parts/SelectFilter');
var DateFilter = require('./parts/DateFilter');

module.exports = React.createClass({

    getInitialState: function() {
        return {
            filterConfiguration: [],
            filterData: {}
        };
    },

    createSelectFilter: function(filterProperty){
        return (
            <SelectFilter
                ref={filterProperty.serverProperty}
                filterProperties={filterProperty}
                filterChange={this.filterChange}/>
        );
    },

    createDateFilter: function(filterProperty){
        return (
            <DateFilter
                ref={filterProperty.serverProperty}
                filterProperties={filterProperty}
                filterChange={this.filterChange}/>
        );
    },
    filterChange: function(){
        setTimeout(function(){
            var data = {};

            _.forEach(this.refs, function(filterData, filterName){
                var filter = filterData.refs[filterName],
                    filterValue;

                if(filter.state.value !== undefined ){
                    filterValue = this.getSelectValue(filterName, filter);
                } else {
                    filterValue = this.getInputValue(filterName, filter);
                }

                FilterStore.setFilteredValue(filterName, filterValue);
                data[filterName] = filterValue
            }.bind(this));
        }.bind(this), 1);
    },

    getSelectValue: function(filterName, filter){
        return filter.state.value;
    },

    getInputValue: function(filterName, filter){
        return filter.getDOMNode().value;
    },

    componentDidMount: function(){
        FilterStore.listen(this.filterStoreUpdated);
    },

    filterStoreUpdated: function(){
        this.setState({
            filterConfiguration: FilterStore.filterConfiguration
        })
    },

    render: function(){
        var renderedFilters = this.state.filterConfiguration.map(function(filterProperty){
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