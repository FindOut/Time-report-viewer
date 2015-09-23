var webpack = require('webpack');
var path = require('path');

module.exports = {
    devtool: 'inline-source-map',

    entry: {
        app: ['webpack-dev-server/client?http://localhost:3001', 'webpack/hot/dev-server', './source/index'],
        plugins: ['react','reflux','lodash','react-highcharts','moment']
    },


    output: {
        path: path.join(__dirname, '/app/'),
        filename: 'bundle.js',
        publicPath: 'http://localhost:3001/app/'
    },

    plugins: [
        new webpack.HotModuleReplacementPlugin(),
        new webpack.optimize.CommonsChunkPlugin(/* chunkName= */"plugins", /* filename= */"plugins.bundle.js")
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
            { test: /\.(ttf|eot|svg|woff).*$/, loaders: ['file']},
            {
                test: /\.(jpe?g|png|gif|svg)$/i,
                loaders: [
                    'file?hash=sha512&digest=hex&name=[hash].[ext]',
                    'image-webpack?bypassOnDebug&optimizationLevel=7&interlaced=false'
                ]
            }
        ]

    }
};