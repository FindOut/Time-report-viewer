var React = require('react');
var _ = require('lodash');
var FilterStore = require('./FilterStore');

require('./filter.scss');
var Select = require('./parts/select');
module.exports = React.createClass({
    datePickers: [],

    getInitialState: function() {
        return {
            filterConfiguration: [],
            filterData: {}
        };
    },

    createSelectFilter: function(filterProperty){
        return (
            <Select ref={filterProperty.serverProperty} filterProperty={filterProperty} filterChange={this.filterChange}/>
        );
    },

    createDateFilter: function(filterProperty){
        this.datePickers.push("filter_" +filterProperty.serverProperty);
        return (
            <li key={filterProperty.serverProperty}>
                <label>{filterProperty.label}: </label><br/>
                <input
                    id={"filter_" + filterProperty.serverProperty}
                    ref={filterProperty.serverProperty}
                    onChange={this.filterChange}/>
            </li>
        );
    },
    filterChange: function(){
        setTimeout(function(){
            console.log('filtering');
            var data = {};
            _.forEach(this.refs, function(filterData, filterName){
                var filterValue;
                if(filterData.refs[filterName] !== undefined ){
                    filterValue = filterData.refs[filterName].state.value;
                    FilterStore.setFilteredValue(filterName, filterValue);
                } else {
                    filterValue = filterData.getDOMNode().value;
                    FilterStore.setFilteredValue(filterName, filterValue);
                }

                data[filterName] = filterValue
            });
        }.bind(this), 1);
    },
    initDatePickers: function () {
        var self = this;
        this.datePickers.forEach(function(idForDatePicker){
            $("#"+idForDatePicker).datepicker({onSelect: self.filterChange,  dateFormat: 'yy-mm-dd' });
        });
    },

    componentDidMount: function(){
        FilterStore.listen(this.filterStoreUpdated);

        this.initDatePickers();
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