var React = require('react');
var _ = require('lodash');

require('./filter.scss');
var Select = require('./parts/select');
module.exports = React.createClass({
    datePickers: [],

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
            var data = {};
            _.forEach(this.refs, function(filterData, filterName){
                var filterValue;
                if(filterData.refs[filterName] !== undefined ){
                    filterValue = filterData.refs[filterName].state.value;
                } else {
                    filterValue = filterData.getDOMNode().value;
                }

                data[filterName] = filterValue
            });

            this.props.onChange(data);
        }.bind(this), 1);
    },
    initDatePickers: function () {
        var self = this;
        this.datePickers.forEach(function(idForDatePicker){
            $("#"+idForDatePicker).datepicker({onSelect: self.filterChange,  dateFormat: 'yy-mm-dd' });
        });
    },

    getInitialState: function(){
        return {filterData: {}};
    },
    componentDidMount: function(){
        this.initDatePickers();
    },
    render: function(){
        var renderedFilters = this.props.filters.map(function(filterProperty){
            switch (filterProperty.type) {
                case 'select':
                    return this.createSelectFilter(filterProperty);
                case 'date':
                    return this.createDateFilter(filterProperty);
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