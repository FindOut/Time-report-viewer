var React = require('react');

module.exports = React.createClass({
    render: function() {
        var renderedEmployees = this.props.employees.map(function (employee) {
            return (
                <li>
                    {employee.name}
                </li>
            )
        });

        return (
            <div id="employeesInSelection">
                <h4>Employees</h4>
                <ul>
                    {renderedEmployees}
                </ul>
            </div>
        )
    }
});