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
        var xhr = new XMLHttpRequest();
        xhr.open('GET', AppConfig.serverURL + uri, true);
        xhr.setRequestHeader('x-auth-token', AppConfig.accessToken.access_token);

        xhr.onload = function (e) {
            if (this.status == 200) {
                window.open(AppConfig.serverURL.replace('/api', '') + '/page/fetchFile/' + '?serverFileName=' + this.response);
            }
        };
        xhr.send();
    },

    downloadWithData: function(uri, data){
        var xhr = new XMLHttpRequest(),
            formData = new FormData();

        formData.append('file', data);

        xhr.open('POST', AppConfig.serverURL + uri, true);
        xhr.setRequestHeader('x-auth-token', AppConfig.accessToken.access_token);
        xhr.responseType = 'blob';


        xhr.onload = function (e) {
            if (this.status == 200) {
                window.open(AppConfig.serverURL.replace('/api', '') + '/page/fetchFile/' + '?serverFileName=' + this.response);
            }
        };
        xhr.send(formData);
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