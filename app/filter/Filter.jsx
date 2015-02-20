var React = require('react');

module.exports = React.createClass({
    datePickers: [],

    createSelectFilter: function(filterProperty){
        var renderedOptions = filterProperty.data.map(function(dataItem){
            return <option value={dataItem.id}>{dataItem.name}</option>
        });
        var multiple = filterProperty.multiple ? 'multiple' : '';

        return (
            <span>
                <label>{filterProperty.label}: </label>
                <select multiple={filterProperty.multiple} ref={filterProperty.serverProperty}>
                        {renderedOptions}
                </select>
            </span>
        );
    },
    createDateFilter: function(filterProperty){
        this.datePickers.push("filter_" +filterProperty.serverProperty);
        return (
            <span>
                <label>{filterProperty.label}</label>
                <input id={"filter_" + filterProperty.serverProperty} ref={filterProperty.serverProperty}/>
            </span>
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
            <div>
                <form onChange={this.filterChange}>
                    {renderedFilters}
                </form>
            </div>
        )
    }
});