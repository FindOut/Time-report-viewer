var React = require('react');

module.exports = React.createClass({
    render: function(){
        var filterID = "filter_" + this.props.filterProperty.serverProperty;

        if($("#" + filterID + ".hasDatepicker").length === 0){
            $("#"+filterID).datepicker(
                {
                    onSelect: this.props.filterChange,
                    dateFormat: 'yy-mm-dd'
                }
            );
        }

        return (
            <li key={this.props.filterProperty.serverProperty}>
                <label>{this.props.filterProperty.label}: </label><br/>
                <input
                    id={filterID}
                    ref={this.props.filterProperty.serverProperty}/>
            </li>
        );
    }
});