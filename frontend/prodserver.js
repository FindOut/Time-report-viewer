var webpack = require('webpack');
var config = require('./webpack.config');
var express = require('express');

var app = express();

app.use('/', express.static(__dirname +'/app'));
app.get('/', function(req, res) {
    res.sendFile(__dirname + '/app/index.html');
});
app.listen(3000);


/* Dev */
new webpack(config).run(function(err){
    console.log(err);
});