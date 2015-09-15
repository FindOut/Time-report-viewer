var React = require('react');

var FilterStore = require('../FilterStore');

module.exports = React.createClass({
    updateFilterStore: function(date){
        var filterName = this.props.filterProperties.serverProperty;
        FilterStore.setFilteredValue(filterName, date);
    },

    render: function(){
        var filterID = "filter_" + this.props.filterProperties.serverProperty;

        if($("#" + filterID + ".hasDatepicker").length === 0){
            $("#"+filterID).datepicker(
                {
                    onSelect: this.updateFilterStore,
                    dateFormat: 'yy-mm-dd'
                }
            );
        }

        return (
            <li key={this.props.filterProperties.serverProperty}>
                <input
                    id={filterID}
                    ref={this.props.filterProperties.serverProperty}
                    placeholder={"Filter " + this.props.filterProperties.label}/>
            </li>
        );
    }
});