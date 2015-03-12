var React = require('react');
var _ = require('lodash');

module.exports = React.createClass({
    getInitialState: function(){
        return {
            filterValue: '',
            filteredItems: [],
            value: []
        }
    },

    filterItems: function(event){
        var filterInputValue = event.target.value,
            filteredItems = _.filter(this.props.filterProperty.data, function (item) {
                var itemName = item.name.toLowerCase();
                return itemName.indexOf(filterInputValue.toLowerCase()) > -1;
            });

        this.setState({
            filterValue: filterInputValue,
            filteredItems: filteredItems
        });
    },

    render: function(){
        var filterProperty = this.props.filterProperty,
            items = this.state.filterValue !== '' ? this.state.filteredItems : this.props.filterProperty.data;

        var renderedOptions;
        if(items !== undefined){
            renderedOptions= items.map(function(dataItem){
                return <option key={dataItem.id} value={dataItem.id}>{dataItem.name}</option>
            });
        }

        var filterSelect;
        if(filterProperty.multiple){
            var placeholder = "Filter " + filterProperty.label;
            filterSelect = (<input onChange={this.filterItems} placeholder={placeholder}/>)
        }
        return (
            <li key={filterProperty.serverProperty}>
                <label>{filterProperty.label}: </label><br/>
                {filterSelect}
                <select
                    multiple={filterProperty.multiple}
                    ref={filterProperty.serverProperty}
                    onChange={this.props.filterChange}>
                    {renderedOptions}
                </select>
            </li>
        )
    }
});