var React = require('react'),
    WorkdayStore = require('../stores/WorkdayStore'),
    ActivityStore = require('../stores/ActivityStore'),
    UserStore = require('../stores/UserStore'),
    WorkdaysDetails = require('./workdaysDetails'),
    WorkdaysSummary = require('./workdaysSummary'),
    UsersInWorkdaysSelection =require('./usersInWorkdaysSelection');


module.exports = React.createClass({
    getInitialState: function(){
        return {
            workdays: [],
            currentActivities: [],
            currentUsers: []
        };
    },

    workdaysUpdated: function(workdays){
        var currentActivityIds = WorkdayStore.getCurrentActivities();
        //console.log(currentActivityIds);
        var currentActivities = _.filter(ActivityStore.getActivities(), function(activity){
            return _.contains(currentActivityIds, activity.id);
        });

        var currentUserIds = WorkdayStore.getCurrentUsers();
        var currentUsers = _.filter(UserStore.getUsers(), function(user){
            return _.contains(currentUserIds, user.id);
        });
        this.setState({
            workdays: workdays,
            currentActivities: currentActivities,
            currentUsers: currentUsers
        })
    },

    componentDidMount: function(){
        WorkdayStore.listen(this.workdaysUpdated);
    },

    render: function(){
        return (
            <div id="workdayViewer">
                <div style={{display:'block'}}>
                    <WorkdaysSummary workdays={this.state.workdays} />
                    <UsersInWorkdaysSelection users={this.state.currentUsers}/>
                </div>
                <WorkdaysDetails
                    workdays={this.state.workdays}
                    activities={this.state.currentActivities}/>
            </div>
        );
    }
});