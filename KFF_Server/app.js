/**
 * The file to start a server
 */

var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var session = require('express-session');
var MongoStore = require('connect-mongo')(session);
var fileUpload = require('express-fileupload');

//connect to MongoDB
mongoose.connect('mongodb://localhost/kff', function ()
{
    console.log('mongodb connected!');
});

var db = mongoose.connection;

//use sessions for tracking login and they will expire after 1 hour (1*60*60*1000)
app.use(session({
    secret: 'wikilatic app user shhhh!',
    cookie: {maxAge: 3600000},
    resave: true,
    saveUninitialized: false,
    store: new MongoStore({
        mongooseConnection: db
    })
}));

// parse incoming requests
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(fileUpload());

var path = require('path');
// serve static files from template
app.use(express.static(path.join(__dirname, '/public')));
// set the path for views
app.set('views', path.join(__dirname,'/app/views'));

// set view engine for pug
app.set('view engine', 'pug');

// set the routes
var routes = require('./app/routes/server.routes');
app.use('/', routes);

// catch 404 and forward to error handler
app.use(function (req, res, next)
{
    var err = new Error('File Not Found!');
    err.status = 404;
    next(err);
});

// error handler
// define as the last app.use callback
app.use(function (err, req, res, next)
{
    res.status(err.status || 500);
    res.send(err.message);
});

// console msg if all ready
app.listen(3000, function ()
{
    console.log('WikiLatic app listening on port 3000!')
});
	
module.exports = app;