/**
 * The app facebook user model to be created as a collection in mongodb
 */

var mongoose = require('mongoose');
//var bcrypt = require('bcrypt-nodejs');

var AppFbUserSchema = new mongoose.Schema({
    name:
        {
            type: String,
            unique: false,
            required: true,
            trim: true
        },
    facebookId:
        {
            type: String,
            required: true,
            unique: true
        },
    gender:
        {
            type: String,
            unique: false,
            required: true,
            trim: true
        },
    birthday:
        {
            type: String,
            unique: false,
            required: false,
            trim: false
        },
});

// create index
AppFbUserSchema.index({facebookId: 1});

//authenticate input against database
AppFbUserSchema.statics.authenticate = function (facebookId, callback)
{
    AppFbUser.findOne({ facebookId: facebookId })
        .exec(function (error, user)
        {
            if (error)
            {
                return callback(error);
            }
            else if (!user)
            {
                var err = new Error('User not found!');
                err.status = 401;
                return callback(err);
            }
            else
            {
                return callback();
            }
        });
};

AppFbUserSchema.statics.findAllUsers = function (callback)
{
    AppFbUser.find({})
        .lean()
        .exec(function (error, users)
        {
            if (error)
            {
                return callback(error);
            }
            else if (users)
            {
                return callback(null, users);
            }
        });
};

var AppFbUser = mongoose.model('AppFbUser', AppFbUserSchema, 'appFbUsers');

module.exports = AppFbUser;