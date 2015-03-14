var React = require('react');

module.exports = React.createClass({
    render: function(){
        var filterID = "filter_" + this.props.filterProperties.serverProperty;

        if($("#" + filterID + ".hasDatepicker").length === 0){
            $("#"+filterID).datepicker(
                {
                    onSelect: this.props.filterChange,
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