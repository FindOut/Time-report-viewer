var webpack = require('webpack');

module.exports = {

    entry: [
        './app'
    ],

    output: {
        path: __dirname + '/app/',
        //publicPath: 'http://localhost:3000/app/',
        filename: 'bundle.js'
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
            { test: /\.css$/, loaders: ['style', 'css'] },
            { test: /\.scss$/, loaders: ['style', 'css', 'autoprefixer-loader?browsers=last 2 version', 'sass']},
            { test: /\.jsx$/, loaders: ['react-hot', 'jsx'] },
            { test: /\.yaml$/, loaders: ['json', 'yaml'] },
            { test: /\.(ttf|eot|svg|woff).*$/, loaders: ['file']}
        ]
    }
};