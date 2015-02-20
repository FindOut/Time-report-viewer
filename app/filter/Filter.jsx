var React = require('react');

require('./filter.scss');
module.exports = React.createClass({
    datePickers: [],

    createSelectFilter: function(filterProperty){
        var renderedOptions = filterProperty.data.map(function(dataItem){
            return <option value={dataItem.id}>{dataItem.name}</option>
        });
        var multiple = filterProperty.multiple ? 'multiple' : '';

        return (
            <li>
                <label>{filterProperty.label}: </label><br/>
                <select multiple={filterProperty.multiple} ref={filterProperty.serverProperty}>
                        {renderedOptions}
                </select>
            </li>
        );
    },
    createDateFilter: function(filterProperty){
        this.datePickers.push("filter_" +filterProperty.serverProperty);
        return (
            <li>
                <label>{filterProperty.label}: </label><br/>
                <input id={"filter_" + filterProperty.serverProperty} ref={filterProperty.serverProperty}/>
            </li>
        );
    },
    filterChange: function(){
        setTimeout(function(){
            var data = {};
            _.forEach(this.refs, function(filterData, filterName){
                data[filterName] = filterData.state.value || filterData.getDOMNode().value;
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
                <form onChange={this.filterChange}>
                    <ul>
                        {renderedFilters}
                    </ul>
                </form>
            </div>
        )
    }
});