

var webpack = require('webpack')
var helpers = require('./webpack.helpers')
var path = require('path')
var CleanWebpackPlugin = require('clean-webpack-plugin')

function exports(env, argv) {
    var isProd = argv.mode === "production"

    var commonPlugins = [
        new CleanWebpackPlugin(['dist']),
        new webpack.DefinePlugin({
            __PRODUCTION_MODE__: isProd,
            __APP_VERSION__: JSON.stringify(process.env.SPLINE_VERSION)
        }),

        // Workaround for Critical dependency
        // The request of a dependency is an expression in ./node_modules/@angular/core/fesm5/core.js
        new webpack.ContextReplacementPlugin(
            /\@angular(\\|\/)core(\\|\/)fesm5/,
            helpers.root('./src'),
            {}
        )
    ]

    return {
        entry: './src/main.ts',
        output: {
            filename: 'bundle.[name].js',
            path: path.resolve(__dirname, 'dist'),
            publicPath: "assets/"
        },
        optimization: {
            splitChunks: {
                chunks: 'all'
            }
        },
        devtool: 'source-map',
        resolve: {
            extensions: ['.webpack.js', '.web.js', '.ts', '.tsx', '.js', '.jsx', '.less', '.css', '.html']
        },
        plugins: commonPlugins.concat(isProd
            ? [/* prod build plugins */]
            : [/* dev build plugins */]
        ),
        module: {
            rules: [
                {test: /\.exec\.js$/, include: /src\/third-party-scripts/, loaders: ['script-loader']},
                {
                    test: /\.ts$/,
                    exclude: /node_modules/,
                    loaders: ['awesome-typescript-loader', 'angular2-template-loader']
                },
                {test: /\.(html|css)$/, exclude: /node_modules/, loader: 'raw-loader'},
                {test: /\.less$/, exclude: /node_modules/, loader: 'raw-loader!less-loader'},
                {test: /\.css/, include: /node_modules/, loaders: ['style-loader', 'css-loader']},
                {
                    test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                    loader: "url-loader?limit=10000&mimetype=application/font-woff"
                },
                {test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: "url-loader"},
                {test: /\.(png|gif)$/, loader: "url-loader"}
            ]
        },
        devServer: {
            contentBase: path.join(__dirname, "src"),
            historyApiFallback: {
                index: '/'
            },
            hot: false,
            proxy: {
                "/rest": {
                    target: "http://localhost:3004",
                    pathRewrite: {"^/rest": ""}
                }
            }
        }
    }
}

module.exports = exports