var React = require('react');

var WorkdaysStore = require('../workdays/WorkdaysStore');

module.exports = React.createClass({
    getInitialState: function(){
        return {workdays: []};
    },
    onStoreChange: function(storeData){
        this.setState({workdays: storeData});
    },
    componentDidMount: function(){
        this.unsubscribe = WorkdaysStore.listen(this.onStoreChange);
    },
    componentWillUnmount: function(){
        this.unsubscribe();
    },

    render: function(){
        var t = this.state.workdays.map(function(workday){
            return (<span>{workday.hours}</span>);
        });

        return (<div>{t}</div>);
    }
});