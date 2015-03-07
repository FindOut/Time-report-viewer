var webpack = require('webpack');
var WebpackDevServer = require('webpack-dev-server');
var config = require('./webpack.dev.config');

var express = require('express');
var proxy = require('proxy-middleware');
var url = require('url');

/* Proxy */
var app = express();
app.use('/app', proxy(url.parse('http://localhost:3001/app')));

app.get('/*', function(req, res) {
    res.sendFile(__dirname + '/app/index.html');
});
app.listen(3000);


/* Dev */
new WebpackDevServer(webpack(config), {
    contentBase: __dirname,
    publicPath: config.output.publicPath,
    hot: true
}).listen(3001, 'localhost', function (err) {
    if (err) {
        console.log(err);
    }
});