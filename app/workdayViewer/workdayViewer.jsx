var React = require('react');
var WorkdayStore = require('../stores/WorkdayStore');
var UserStore = require('../stores/UserStore');
var UsersInWorkdaysSelection =require('./usersInWorkdaysSelection');

module.exports = React.createClass({
    getInitialState: function(){
        return {
            currentUsers: []
        };
    },

    workdaysUpdated: function(){
        var currentUserIds = WorkdayStore.getCurrentUsers();
        var currentUsers = _.filter(UserStore.getUsers(), function(user){
            return _.contains(currentUserIds, user.id);
        });
        this.setState({
            currentUsers: currentUsers
        })
    },

    componentDidMount: function(){
        WorkdayStore.listen(this.workdaysUpdated);
    },

    render: function(){

        return (
            <UsersInWorkdaysSelection users={this.state.currentUsers}/>
        )
    }
});