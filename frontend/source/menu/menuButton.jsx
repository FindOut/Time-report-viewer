var React = require('react');

require("image!./menu.png");
require('./menu.scss');

var MenuItem = require("./parts/menuItem");

module.exports = React.createClass({
    getInitialState: function(){
        return {visibility: 'hidden'};
    },
    toggleMenu: function(){
        var visibility = this.state.visibility === 'hidden' ? 'visible' : 'hidden';
        this.setState({visibility: visibility})
    },
    render: function(){
        var menuItems = this.props.items.map(function(item){
            return (<MenuItem {...item}/>)
        });

        return (<div id="menu" onClick={this.toggleMenu}>
            <div className="list" style={{visibility: this.state.visibility}}>
                {menuItems}
            </div>
        </div>)
    }
});