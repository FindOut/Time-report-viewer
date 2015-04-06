var AppConfig = require('./AppConfig');

module.exports = {
    get: function(uri, callback, data){
        $.ajax({
            url: AppConfig.serverURL + uri,
            crossDomain: true,
            data: data,
            headers : {
                'x-auth-token' : AppConfig.accessToken.access_token
            }
        }).then(function (data) {
            callback(data);
        }.bind(this));
    },
    login: function(credentials, statusCodeCallbacks){
        console.log('logging in');
        $.ajax({
            url: AppConfig.serverURL + '/login',
            type: 'POST',
            dataType: 'json',
            contentType: "application/json; charset=UTF-8",
            data: JSON.stringify(credentials),
            statusCode: statusCodeCallbacks
        });
    }
};