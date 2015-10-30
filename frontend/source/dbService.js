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

        var createObjectURL = function (file) {
                if (window.webkitURL) {
                    return window.webkitURL.createObjectURL(file);
                } else if (window.URL && window.URL.createObjectURL) {
                    return window.URL.createObjectURL(file);
                } else {
                    return null;
                }
            },
            xhr = new XMLHttpRequest();
        xhr.open('GET', AppConfig.serverURL + uri, true);
        xhr.setRequestHeader('x-auth-token', AppConfig.accessToken.access_token);
        xhr.responseType = 'blob';

        xhr.onload = function (e) {
            if (this.status == 200) {
                var url = createObjectURL(new Blob([this.response], {
                    type: 'application/vnd.ms-excel'
                }));
                var link = document.createElement('A');
                link.setAttribute('href', url);
                link.setAttribute('Download', 'profitability_basis.xlsx');
                link.appendChild(document.createTextNode('Download'));
                link.click();
                //document.getElementsByTagName('body')[0].prependChild(link);

            }
        };
        xhr.send();
    },

    downloadWithData: function(uri, data){
        var createObjectURL = function (file) {
                if (window.webkitURL) {
                    return window.webkitURL.createObjectURL(file);
                } else if (window.URL && window.URL.createObjectURL) {
                    return window.URL.createObjectURL(file);
                } else {
                    return null;
                }
            },
            xhr = new XMLHttpRequest(),
            formData = new FormData();

        formData.append('file', data);



        xhr.open('POST', AppConfig.serverURL + uri, true);
        xhr.setRequestHeader('x-auth-token', AppConfig.accessToken.access_token);
        xhr.responseType = 'blob';


        xhr.onload = function (e) {
            if (this.status == 200) {
                var url = createObjectURL(new Blob([this.response], {
                    type: 'application/vnd.ms-excel'
                }));
                var link = document.createElement('A');
                link.setAttribute('href', url);
                link.setAttribute('Download', 'Lönsamhetsmodell.xlsx');
                link.appendChild(document.createTextNode('Download'));
                link.click();
                //document.getElementsByTagName('body')[0].prependChild(link);

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