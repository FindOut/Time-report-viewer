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
    download: function(uri){
        console.log(uri);
        //var xmlhttp =new XMLHttpRequest();
        //xmlhttp.open("GET", AppConfig.serverURL + '/login');
        //xmlhttp.setRequestHeader('x-auth-token', AppConfig.accessToken.access_token);
        ////xmlhttp.contentDisposition = 'attachment';
        //xmlhttp.send();
        $.ajax({
            type: 'POST',
            url: AppConfig.serverURL + uri,
            crossDomain: true,
            headers : {
                'x-auth-token' : AppConfig.accessToken.access_token
            }

        });
    },
    login: function(credentials, statusCodeCallbacks){
        $.ajax({
            url: AppConfig.serverURL + '/login',
            crossDomain: true,
            type: 'POST',
            dataType: 'json',
            contentType: "application/json; charset=UTF-8",
            data: JSON.stringify(credentials),
            statusCode: statusCodeCallbacks
        });
    }
};