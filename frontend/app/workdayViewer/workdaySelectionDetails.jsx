var React = require('react');

module.exports = React.createClass({
    getTotalHoursInSelection: function(){
        console.log(this.props.workdays);
        if(this.props.workdays.length > 0){
            var selectionHours = this.props.workdays.map(function(workday){
                return workday.hours
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
            <div id="workdaysDetails">
                <h3>Total Hours</h3>
                {totalHoursInSelection}
            </div>
        )
    }
});