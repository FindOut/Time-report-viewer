var React = require('react');

module.exports = React.createClass({
    getTotalHoursInSelection: function(){
        if(this.props.activityReports.length > 0){
            var selectionHours = this.props.activityReports.map(function(activityReport){
                return activityReport.hours
            }).reduce(function(total, currentValue){
                return total + currentValue
            });

            return Math.round(selectionHours*100)/100;
        } else {
            return 0;
        }

    },
    render: function(){
        var totalHoursInSelection = this.getTotalHoursInSelection();
        return (
            <div id="activityReportsDetails">
                <h4>Total Hours</h4>
                {totalHoursInSelection}
            </div>
        )
    }
});