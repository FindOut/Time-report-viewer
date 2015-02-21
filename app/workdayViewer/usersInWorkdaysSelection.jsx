var React = require('react');

module.exports = React.createClass({
    render: function() {
        var renderedUsers = this.props.users.map(function (user) {
            return (
                <li>
                    {user.name}
                </li>
            )
        });

        return (
            <div id="usersInSelection">
                <h4>Users</h4>
                <ul>
                    {renderedUsers}
                </ul>
            </div>
        )
    }
});