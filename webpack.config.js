var webpack = require('webpack');

module.exports = {

    entry: [
        './app'
    ],

    output: {
        path: __dirname + '/app/',
        filename: 'bundle.min.js'
    },

    plugins: [
        new webpack.optimize.DedupePlugin(),
        new webpack.optimize.UglifyJsPlugin({minimize: true})
    ],

    resolve: {
        extensions: ['', '.js', '.jsx']
    },

    module: {
        loaders: [
            { test: /\.css$/, loaders: ['style', 'css?minimize'] },
            { test: /\.jsx$/, loaders: ['react-hot', 'jsx'] },
            { test: /\.yaml$/, loaders: ['json', 'yaml'] }
        ]
    }
};