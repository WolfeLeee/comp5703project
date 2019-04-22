/**
 * The app user model to be created as a collection in mongodb
 */

var mongoose = require('mongoose');
//var bcrypt = require('bcrypt-nodejs');

var AppUserSchema = new mongoose.Schema({
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
            required: true,
            trim: true
        },
    password:
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
AppUserSchema.index({email: 1});

//authenticate input against database
AppUserSchema.statics.authenticate = function (email, password, callback)
{
    AppUser.findOne({ email: email })
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

AppUserSchema.statics.findAllUsers = function (callback)
{
    AppUser.find({})
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

var AppUser = mongoose.model('AppUser', AppUserSchema, 'appUsers');

module.exports = AppUser;