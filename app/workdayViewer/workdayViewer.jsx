var React = require('react'),
    WorkdayStore = require('../stores/WorkdayStore'),
    UserStore = require('../stores/UserStore'),
    WorkdaysSummary = require('./workdaysSummary');
    UsersInWorkdaysSelection =require('./usersInWorkdaysSelection');


module.exports = React.createClass({
    getInitialState: function(){
        return {
            currentUsers: []
        };
    },

    workdaysUpdated: function(workdays){
        var currentUserIds = WorkdayStore.getCurrentUsers();
        var currentUsers = _.filter(UserStore.getUsers(), function(user){
            return _.contains(currentUserIds, user.id);
        });
        this.setState({
            workdays: workdays,
            currentUsers: currentUsers
        })
    },

    componentDidMount: function(){
        WorkdayStore.listen(this.workdaysUpdated);
    },

    render: function(){
        return (
            <div id="workdayViewer">
                <WorkdaysSummary workdays={this.state.workdays} />
                <UsersInWorkdaysSelection users={this.state.currentUsers}/>
            </div>
        );
    }
});