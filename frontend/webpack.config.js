var webpack = require('webpack');
var path = require('path');

module.exports = {

    entry: {
        app: "./source/index",
        plugins: ['react','reflux','lodash','react-highcharts','moment']
    },

    output: {
        path: path.join(__dirname, '/app/'),
        //publicPath: 'http://localhost:3000/app/',
        filename: 'bundle.js'
    },

    plugins: [
        new webpack.optimize.DedupePlugin(),
        new webpack.optimize.UglifyJsPlugin({minimize: true}),
        new webpack.optimize.CommonsChunkPlugin(/* chunkName= */"plugins", /* filename= */"plugins.bundle.js")
    ],

    resolve: {
        extensions: ['', '.js', '.jsx']
    },

    module: {
        loaders: [
            { test: /\.png/, loader: 'url-loader?limit=100000&mimetype=image/png' },
            { test: /\.css$/, loaders: ['style', 'css'] },
            { test: /\.scss$/, loaders: ['style', 'css', 'autoprefixer-loader?browsers=last 2 version', 'sass']},
            { test: /\.jsx$/, loaders: ['react-hot', 'jsx'] },
            { test: /\.yaml$/, loaders: ['json', 'yaml'] },
            { test: /\.(ttf|eot|svg|woff).*$/, loaders: ['file']}
        ]
    }
};