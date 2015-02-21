var React = require('react');
var WorkdayStore = require('../stores/WorkdayStore');

module.exports = React.createClass({
    getInitialState: function(){
        return {
            currentUsers: []
        };
    },

    workdaysUpdated: function(){
        this.setState({
            currentUsers: WorkdayStore.getCurrentUsers()
        })
    },

    componentDidMount: function(){
        WorkdayStore.listen(this.workdaysUpdated);
    },

    render: function(){



        return (
            <div>
                {this.state.currentUsers}
            </div>
        )
    }
});