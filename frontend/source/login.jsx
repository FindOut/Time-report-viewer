require('../source/login.scss');
var React = require('react');
var LoginStore = require('../source/stores/LoginStore');

module.exports = React.createClass({
    username: '',
    password: '',
    login: function(event){
        event.preventDefault(); // Prevents form from reloading page
        LoginStore.login(this.username, this.password)
    },
    handleInputChange: function(event){
        var field = event.target.name;

        this[field] = event.target.value;
    },
    render: function(){
        return (
            <div id="login">
                <div>Please Login (Confluence credentials)</div>
                <form>
                    <p>
                        <label for="username">Username:</label>
                        <input type="text" name='username' onChange={this.handleInputChange} />
                    </p>

                    <p>
                        <label for="password">Password:</label>
                        <input type="password" name='password' onChange={this.handleInputChange} />
                    </p>

                    <p>
                        <button onClick={this.login}>Login</button>
                    </p>
                </form>
            </div>
        );
    }
});