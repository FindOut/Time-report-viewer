var React = require('react');
var DBService = require('../../DBService');

module.exports = React.createClass({

    itemClick: function(event){
        event.preventDefault();

        DBService.get(this.props.url);
    },
    render: function(){
        return (
            <div href={this.props.url} className="listItem" onClick={this.itemClick}>{this.props.text}</div>
        )
    }
});