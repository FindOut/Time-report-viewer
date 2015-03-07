var webpack = require('webpack');
var path = require('path');

module.exports = {
    devtool: 'inline-source-map',

    entry: [
        'webpack-dev-server/client?http://localhost:3001',
        'webpack/hot/dev-server',
        './app/index'
    ],

    output: {
        path: path.join(__dirname, '/app/'),
        filename: 'bundle.js',
        publicPath: 'http://localhost:3001/app/'
    },

    plugins: [
        new webpack.HotModuleReplacementPlugin(),
    ],

    resolve: {
        extensions: ['', '.js', '.jsx']
    },

    module: {

        preLoaders: [
            {
                test: /\.js?$/,
                exclude: path.join(__dirname, '/node_modules'),
                loader: 'jsxhint'
            }
        ],

        loaders: [
            { test: /\.css$/, loaders: ['style', 'css'] },
            { test: /\.scss$/, loaders: ['style', 'css', 'autoprefixer-loader?browsers=last 2 version', 'sass']},
            { test: /\.jsx$/, loaders: ['react-hot', 'jsx'] },
            { test: /\.yaml$/, loaders: ['json', 'yaml'] },
            { test: /\.(ttf|eot|svg|woff).*$/, loaders: ['file']}
        ]

    }
};