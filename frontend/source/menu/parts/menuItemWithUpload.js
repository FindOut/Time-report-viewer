var React = require('react');
var DBService = require('../../DBService');

module.exports = React.createClass({

    itemClick: function(event){
        //event.preventDefault();
        this.refs.file.getDOMNode().click();
        //
        //DBService.download(this.props.url);
    },

    onChange: function(){
        console.log();
        var data = this.refs.file.getDOMNode().files[0];
        DBService.downloadWithData('/export/profitabilityDashboard', data);
    },

    render: function(){
        //<div href="/export/profitabilityDashboard" className="listItem" onClick={this.itemClick}>Profitability dashboard</div>
        return (
            <div className="listItem" onClick={this.itemClick}>
                Profitability dashboard

                <form hidden>
                    <input className="listItem" type='file' name='file' ref='file' onChange={this.onChange}/>
                </form>
            </div>
        );
    }
});