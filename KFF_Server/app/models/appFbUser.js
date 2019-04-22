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
    gender:
        {
            type: String,
            unique: false,
            required: true,
            trim: true
        },
    email:
        {
            type: String,
            unique: true,
            required: false,
            trim: true
        },
    facebookId:
        {
            type: String,
            required: true
        },
    birthday:
        {
            type: String,
            unique: false,
            required: true,
            trim: false
        },
});

// create index
AppFbUserSchema.index({facebookId: 1});

//authenticate input against database
AppFbUserSchema.statics.authenticate = function (email, password, callback)
{
    AppFbUser.findOne({ email: email })
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