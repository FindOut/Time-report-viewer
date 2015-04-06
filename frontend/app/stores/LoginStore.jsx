var Reflux = require('reflux'),
    AppConfig = require('../AppConfig'),
    DBService = require('../DBService');

module.exports = Reflux.createStore({
    token: '',
    init: function () {

    },
    isAuthorized: function(){
        return AppConfig.accessToken.access_token !== undefined;
    },

    login: function(username, password){
        console.log('going to login');
        var credentials = {
                username: username,
                password: password
            },
            setToken = function(token){
                AppConfig.accessToken = token;
                this.trigger(token);
            }.bind(this),
            statusCodeCallbacks = {
                    200: function(token){ // OK
                        setToken(token);
                        console.log(token);
                    },
                    400: function(){ // Bad request
                        console.log('bad login');
                    },
                    401: function(data){ // Unauthorised
                        console.log('Wrong username and/or password');
                        console.log(data);
                    }
            };

        console.log(credentials);

        DBService.login(credentials, statusCodeCallbacks);
    }
});
