/**
 * The user model to be created as a collection in mongodb
 */

//var mongoose = require('./db');
var mongoose = require('mongoose');
//var bcrypt = require('bcrypt-nodejs');

var UserSchema = new mongoose.Schema({
    username:
        {
            type: String,
            unique: true,
            required: true,
            trim: true
        },
    password:
        {
            type: String,
            required: true
        }
});

// var UserSchema = new mongoose.Schema({
//     firstName:
//         {
//             type: String,
//             unique: true,
//             required: true,
//             trim: true
//         },
//     lastName:
//         {
//             type: String,
//             unique: true,
//             required: true,
//             trim: true
//         },
//     username:
//         {
//             type: String,
//             unique: true,
//             required: true,
//             trim: true
//         },
//     password:
//         {
//             type: String,
//             required: true
//         },
//     passwordConfirm:
//         {
//             type: String,
//             required: true
//         },
//     email:
//         {
//             type: String,
//             unique: true,
//             required: true,
//             trim: true
//         }
// });

// create index
UserSchema.index({username: 1});

//authenticate input against database
UserSchema.statics.authenticate = function (username, password, callback)
{
    User.findOne({ username: username })
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

            if(password === user.password)
                return callback(null, user);
            else
                return callback();

            // bcrypt.compare(password, user.password, function (err, result)
            // {
            //     if (result === true)
            //         return callback(null, user);
            //     else
            //         return callback();
            // });
        });
};

//hashing a password before saving it to the database
// User.pre('save', function (next)
// {
//     var user = this;
//     bcrypt.hash(user.password, 10, function (err, hash)
//     {
//         if (err)
//         {
//             return next(err);
//         }
//         user.password = hash;
//         next();
//     })
// });

UserSchema.statics.findAllUsers = function (callback)
{
    User.find({})
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

var User = mongoose.model('User', UserSchema, 'users');

module.exports = User;