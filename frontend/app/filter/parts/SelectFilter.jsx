var React = require('react');
var _ = require('lodash');

module.exports = React.createClass({
    filterChange: function(event){
        this.setState({
            filteredItems: this.filterItems(event.target.value.toLowerCase())
        });
    },

    getItems: function(){
        var items = this.state === null ?  this.props.filterProperties.data : this.state.filteredItems;
        return items || [];
    },

    filterItems: function(filterValue){
        return _.filter(this.props.filterProperties.data, function (item) {
            return item.name.toLowerCase().indexOf(filterValue) > -1;
        });
    },

    render: function(){
        var filterProperties = this.props.filterProperties,
            renderedOptions = this.getItems().map(function(dataItem){
                if(typeof dataItem === 'object'){
                    return <option key={dataItem.id} value={dataItem.id}>{dataItem.name}</option>
                } else {
                    return <option key={dataItem} value={dataItem}>{dataItem}</option>
                }
            });

        return (
            <li key={filterProperties.serverProperty}>
                <input className="selectFilter" onChange={this.filterChange} placeholder={"Filter " + filterProperties.label}/>
                <select
                    multiple={filterProperties.multiple}
                    ref={filterProperties.serverProperty}
                    onChange={this.props.filterChange}>
                    {renderedOptions}
                </select>
            </li>
        )
    }
});