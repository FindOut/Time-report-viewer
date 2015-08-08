var React = require('react'),
    WorkdayStore = require('../stores/WorkdayStore'),
    ActivityStore = require('../stores/ActivityStore'),
    UserStore = require('../stores/UserStore'),
    WorkdaysSummary = require('./workdaysSummary'),
    UsersInWorkdaysSelection = require('./usersInWorkdaysSelection'),
    ActivitiesInWorkdaySelection = require('./activitiesInWorkdaySelection'),
    WorkdaySelectionMetaData = require('./workdaySelectionMetaData'),
    WorkdaySelectionDetails = require('./workdaySelectionDetails');

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

//<WorkdaysSummary workdays={this.state.workdays} />
//<UsersInWorkdaysSelection users={this.state.currentUsers}/>
//<WorkdaysDetails
//    workdays={this.state.workdays}
//    activities={this.state.currentActivities}/>
    render: function(){
        return (
            <div id="workdayViewer">
                <div id="upper">
                    <WorkdaySelectionMetaData workdays={this.state.workdays} />
                    <ActivitiesInWorkdaySelection activities={this.state.currentActivities}/>
                    <UsersInWorkdaysSelection users={this.state.currentUsers}/>
                </div>
                <div id="lower">
                    <WorkdaySelectionDetails workdays={this.state.workdays} />
                </div>
            </div>
        );
    }
});