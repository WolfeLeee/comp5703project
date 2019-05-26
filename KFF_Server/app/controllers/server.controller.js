// Models
var User = require("../models/user");
var Product = require("../models/product");
var AppUser = require("../models/appUser");
var AppFbUser = require("../models/appFbUser");
var Store = require("../models/store");
var ReportedStore = require("../models/reportedStore");
var BrandinStore = require("../models/brandinstore");
var Statistic = require("../models/statistic");
var Version = require("../models/version");

// External librariesreportedStoreFromAndroidAppUsers
var mongoose = require('mongoose');
var csv = require('fast-csv');
var fs = require('fs');
var request = require('request');
var crypto = require('crypto');
var nodemailer = require('nodemailer');
var xoauth2 = require('xoauth2');

// Global variables
var adminId = "";


/* * * * * * * * * *
 * Wolfe's coding  *
 * * * * * * * * * */

// Basic pages with login and register
module.exports.goToLogin = function(req, res, next)
{
    User.countDocuments(function(errorUser, countUser)
    {
        if (errorUser)
        {
            errorUser.status = 400;
            return next(errorUser);
        }
        else
        {
            Version.countDocuments(function(errorVersion, countVersion)
            {
               if(errorVersion)
               {
                   errorVersion.status = 400;
                   return next(errorVersion);
               }
               else
               {
                   if(countUser == 0)
                   {
                       // create admin acc at the beginning
                       var userData = {
                           username: "admin",
                           password: "admin",
                           email: "kinder.foodfinder@gmail.com"
                       };

                       User.create(userData, function(errorCreateUser, user)
                       {
                           if (errorCreateUser)
                           {
                               errorCreateUser.status = 400;
                               return next(errorCreateUser);
                           }
                           else
                           {
                               if(countVersion == 0)
                               {
                                   // create default brand version at the beginning (1.0.0)
                                   var versionBrandData = {
                                       version: "1",
                                       type: "brand"
                                   };

                                   Version.create(versionBrandData, function(errorCreateBrandVersion, versionBrand)
                                   {
                                      if(errorCreateBrandVersion)
                                      {
                                          errorCreateBrandVersion.status = 400;
                                          return next(errorCreateBrandVersion);
                                      }
                                      else
                                      {
                                          // create default store version at the beginning
                                          var versionStoreData = {
                                              version: "1",
                                              type: "store"
                                          };

                                          Version.create(versionStoreData, function(errorCreateStoreVersion, versionStore)
                                          {
                                              if(errorCreateStoreVersion)
                                              {
                                                  errorCreateStoreVersion.status = 400;
                                                  return next(errorCreateStoreVersion);
                                              }
                                              else
                                              {
                                                  res.redirect('/landing');
                                              }
                                          });
                                      }
                                   });
                               }
                               else
                               {
                                   res.redirect('/landing');
                               }
                           }
                       });
                   }
                   else
                   {
                       if(countVersion == 0)
                       {
                           // create default brand version at the beginning (1.0.0)
                           var versionBrandData = {
                               version: "1",
                               type: "brand"
                           };

                           Version.create(versionBrandData, function(errorCreateBrandVersion, versionBrand)
                           {
                               if(errorCreateBrandVersion)
                               {
                                   errorCreateBrandVersion.status = 400;
                                   return next(errorCreateBrandVersion);
                               }
                               else
                               {
                                   // create default store version at the beginning
                                   var versionStoreData = {
                                       version: "1",
                                       type: "store"
                                   };

                                   Version.create(versionStoreData, function(errorCreateStoreVersion, versionStore)
                                   {
                                       if(errorCreateStoreVersion)
                                       {
                                           errorCreateStoreVersion.status = 400;
                                           return next(errorCreateStoreVersion);
                                       }
                                       else
                                       {
                                           res.redirect('/landing');
                                       }
                                   });
                               }
                           });
                       }
                       else
                       {
                           res.redirect('/landing');
                       }
                   }
               }
            });
        }
    });
};

module.exports.showLandingPage = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user)
        {
            if (errorUser)
            {
                return next(errorUser);
            }
            else
            {
                if (user === null)
                {
                    User.findAllUsers(function(error, users)
                    {
                        if(error)
                        {
                            return next(error);
                        }
                        else
                        {
                            adminId = users[0]._id;
                            res.render("landing.pug", {users: users});
                        }
                    });
                }
                else
                {
                    res.redirect('/feature');
                }
            }
        });
};

module.exports.registerLogin = function(req, res, next)
{
    // login
    if (req.body.logusername && req.body.logpassword)
    {
        User.authenticate(req.body.logusername, req.body.logpassword, function (error, user)
        {
            if (error || !user)
            {
                var err = new Error('Wrong username or password!');
                err.status = 401;
                return next(err);
            }
            else
            {
                req.session.userId = user._id;
                return res.redirect('/feature');
            }
        });
    }
    // reset
    else if (req.body.logusernameR && req.body.logpasswordR)
    {
        var newAdmin = {
            username: req.body.logusernameR,
            password: req.body.logpasswordR,
        };

        User.findByIdAndUpdate(adminId, newAdmin, function (error, user)
        {
            if (error)
            {
                var err = new Error('Something wrong when reset admin account!');
                err.status = 401;
                return next(err);
            }
            else if(!user)
            {
                res.send("No admin account found!");
            }
            else
            {
                console.log("Admin account has been reset!");
                res.redirect('/logout');
            }
        });
    }
    // login or reset without filling all fields
    else
    {
        var err = new Error('All fields are required!');
        err.status = 400;
        return next(err);
    }
};

/* * * * * * * * * * * * * * * * * *
 * Statistics Page                 *
 * * * * * * * * * * * * * * * * * */

module.exports.goToFeature = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user)
        {
            if (errorUser)
            {
                return next(errorUser);
            }
            else
            {
                if (user === null)
                {
                    var err = new Error('You are NOT authorized! Please Go back!');
                    err.status = 400;
                    return next(err);
                }
                else
                {
                    Product.find({})
                        .exec(function(errProduct,products)
                        {
                            if(errProduct)
                            {
                                return next(errProduct);
                            }
                            else
                            {

                                Statistic.aggregate([
                                    {"$group":
                                            {
                                                _id: "$brandId",
                                                totalsearch: {$sum:'$count'}
                                            }
                                    },
                                    {"$project":
                                            {
                                                _id:1,
                                                totalsearch:1
                                            }
                                    },
                                    {"$sort":
                                            {
                                                totalsearch: -1
                                            }},
                                    {$limit: 10 }
                                        ],function (err, data)
                                {
                                    var toptenbrands = [];
                                    for(var i = 0 ; i<data.length; i++)
                                    {
                                        for(var j = 0; j < products.length ; j++)
                                        {
                                            if(new String(data[i]._id).valueOf() == new String(products[j]._id).valueOf())
                                            {
                                                var topbrand = {
                                                    brandname: products[j].Brand_Name,
                                                    count: data[i].totalsearch
                                                }
                                                toptenbrands.push(topbrand);
                                            }
                                        }
                                    }
                                    res.render('Statistics.pug',
                                        {
                                            products:products,
                                            topbrands:toptenbrands
                                        });
                                })
                            }
                        })

                }
            }
        });

};


module.exports.GenerateStatistics = async function(req,res,next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user)
        {
            if (errorUser)
            {
                return next(errorUser);
            }
            else
            {
                if (user === null)
                {
                    res.redirect('/');
                }
                else
                {

                    var condition = {};
                    if(new String(req.query.Timeline).toLowerCase().valueOf() == new String("All").toLowerCase().valueOf())
                    {

                    }
                    else if (new String(req.query.Timeline).toLowerCase().valueOf() == new String("Period").toLowerCase().valueOf())
                    {

                        if(req.query.Startdate !== null && req.query.Enddate !== null)
                        {
                            condition.date = {
                                $gt: req.query.Startdate,
                                $lt: req.query.Enddate
                            }
                        }
                        else if(req.query.Enddate == null && req.query.Startdate !== null)
                        {
                            condition.date = {
                                $gt: req.query.Startdate,
                            }
                        }
                        else if (req.query.Startdate == null && req.query.Enddate !== null){
                            condition.date = {
                                $lt: req.query.Enddate
                            }
                        }
                    }
                    if(new String(req.query.Brand).toLowerCase().valueOf() == new String("All").toLowerCase().valueOf()){

                    }
                    else {
                        condition.brandName = req.query.Brand;
                    }
                    if(new String(req.query.Gender).toLowerCase().valueOf() == new String("All").toLowerCase().valueOf()){

                    }
                    else {
                        condition.gender = req.query.Gender;
                    }
                    Statistic.find(condition)
                        .exec(async function(errStatistics,statistics){
                            if(errStatistics)
                            {
                                return next(errStatistics);
                            }
                            else
                            {
                                res.json(statistics);
                            }
                        })
                }
            }
        });

}

/* * * * * * * * * * * * * * * * * *
 * Insert/ Import data             *
 * * * * * * * * * * * * * * * * * */

module.exports.insertnewStore = async function(req,res,next)
{
    var newstore = {
        storeName: req.body.name,
        Address: [],
        Product: [],
    }
    var newaddress = {
        StreetAddress: req.body.address,
        Postcode: req.body.postcode,
        State: req.body.state,
        Lat: req.body.lat,
        Long: req.body.long
    }
    newstore.Address.push(newaddress);
    Store.create(newstore,async function(errStore,store)
    {
        if(errStore)
        {
            var err = new Error('Something wrong when insert new store!');
            err.status = 400;
            return res.send(err);
        }
        else
        {
            res.redirect('/detailstorePage?storeid=' + store._id);
        }
    })
}


module.exports.insertnewBrand = async function (req,res,next)
{
    var newproduct = {
        _id: new mongoose.mongo.ObjectId(),
        Brand_Name: req.body.brandname,
        Category: req.body.brandcategory,
        Accreditation: [],
        Image: null
    }
    newproduct.Accreditation.push({
        Accreditation: req.body.brandaccreditation,
        Rating: req.body.brandrating
    })
    if(req.files == null){
        Product.create(newproduct, async function(errCreate, documents)
        {
            if(errCreate)
            {
                var err = new Error('Something wrong when import new document!');
                err.status = 400;
                return res.send(err);
            }
            else
            {
                res.redirect('/detailproductPage?productid=' + newproduct._id);
            }
        });
    }
    else
    {
        newproduct.Image = newproduct._id;
        Product.create(newproduct, async function(errCreate, documents)
        {
            if(errCreate)
            {
                var err = new Error('Something wrong when import new document!');
                err.status = 400;
                return res.send(err);
            }
            else
            {
                var image = req.files.brandimage;
                var buf = new Buffer(image.data, 'base64');
                fs.writeFile('public/uploads/'+newproduct._id+".jpg",buf,function(err){
                    if(err)
                    {
                        return next(err);
                    }
                    else
                    {
                        res.redirect('/detailproductPage?productid=' + newproduct._id);
                    }
                })
            }
        });
    }

}

module.exports.importCSVFile = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user)
        {
            if (errorUser)
            {
                return next(errorUser);
            }
            else
            {
                if (user === null)
                {
                    var err = new Error('You are NOT authorized! Please Go back!');
                    err.status = 400;
                    return next(err);
                }
                else
                {
                    if (!req.files)
                        return res.status(400).send('No files were uploaded.');

                    var productFile = req.files.file;

                    var temproducts = [];
                    var products =[];

                    csv
                        .fromString(productFile.data.toString(),
                        {
                            headers: true,
                            ignoreEmpty: true
                        })
                        .on("data", function(data)
                        {
                            data['_id'] = new mongoose.Types.ObjectId();
                            temproducts.push(data);
                        })
                        .on("error", function()
                        {
                            res.send("You are importing csv with wrong format!");
                        })
                        .on("end", function()
                        {
                            // For each data in the temproducts, the server will check for identical brand before importing in the database
                            // In specific, brands with identical name will be checked whether or not there is different in their accreditation
                            // If there is difference in Accreditation, the new Accreditation will be pushed into that brand value
                            // instead of creating a new brand
                            temproducts.forEach(function(data){
                                var identicalbrand = false;
                                var identicalaccreditation = false;
                                var position = null;
                                for(var i =0 ; i< products.length; i++){
                                    if(data.Brand_Name.localeCompare(products[i].Brand_Name)==0 && data.Category.localeCompare(products[i].Category) == 0){
                                        identicalbrand = true;
                                        position = i;
                                        for(var j = 0 ; j < products[i].Accreditation.length ; j++){
                                            if( data.Accreditation.localeCompare(products[i].Accreditation[j].Accreditation) == 0){
                                                identicalaccreditation = true;
                                            }
                                        }
                                    }
                                }

                                if(identicalbrand && !identicalaccreditation){
                                    products[position].Accreditation.push({
                                        Accreditation: data.Accreditation,
                                        Rating: data.Rating
                                    });
                                }
                                else if (!identicalaccreditation) {
                                    var newdata = {
                                        Brand_Name: data.Brand_Name.trim(),
                                        Available: data.Available,
                                        Category: data.Category.trim(),
                                        Accreditation:[],
                                        Image:null
                                    }
                                    newdata.Accreditation.push({
                                        Accreditation: data.Accreditation.trim(),
                                        Rating: data.Rating.trim()
                                    })
                                    products.push(newdata);
                                }
                            })


                            // Search for all documents currently in the database
                            // Identical brand will not be added to the database
                            Product.find({}, async function(err, documents)
                            {
                                var insertproducts = [];
                                var updateproducts = [];
                                if(err)
                                {
                                    var err = new Error('Something wrong when query all documents!');
                                    err.status = 400;
                                    return next(err);
                                }
                                else
                                {
                                    // Check for identical data
                                    for(var i = 0 ; i< products.length ; i++){
                                        var identicalName = false;
                                        for(var j = 0; j < documents.length ; j++){
                                            if(products[i].Brand_Name.localeCompare(documents[j].Brand_Name) == 0 && products[i].Category.localeCompare(documents[j].Category) == 0){
                                                identicalName = true;
                                                // Check for identical Accreditation value,
                                                // if there is a new Accreditation, the new Accreditation will be pushed to updateproducts Array
                                                for(var k = 0 ; k <products[i].Accreditation.length ; k ++){
                                                    var identicalAccreditation = false;
                                                    for(var l = 0 ; l < documents[j].Accreditation.length; l++){
                                                        if(products[i].Accreditation[k].Accreditation.localeCompare(documents[j].Accreditation[l].Accreditation) == 0){
                                                            identicalAccreditation = true;
                                                        }
                                                    }
                                                    if(!identicalAccreditation){
                                                        updateproducts.push({
                                                            Accreditation:products[i].Accreditation[k],
                                                            brandid:documents[j]._id
                                                        })
                                                    }

                                                }
                                            }
                                        }
                                        if(!identicalName){
                                            insertproducts.push(products[i]);
                                        }
                                    }
                                    // import the products into mongoDB,
                                    // Check whether the insertproducts array is empty
                                    if(insertproducts.length > 0){
                                        Product.create(insertproducts, async function(errCreate, documents)
                                        {
                                            if(errCreate)
                                            {
                                                var err = new Error('Something wrong when import all documents!');
                                                err.status = 400;
                                                return res.send(err);
                                            }
                                            else
                                            {

                                                if(updateproducts.length>0){
                                                    for(var i = 0 ; i< updateproducts.length ; i++){
                                                    }
                                                    await Product.update({_id:updateproducts[i].brandid},{$push:{Accreditation:updateproducts[i].Accreditation}});
                                                }

                                                res.render('successImport.pug', {numProducts: insertproducts.length, numUpdates: updateproducts.length});
                                            }
                                        });
                                    }
                                    else if(updateproducts.length > 0) {
                                        for(var i = 0 ; i< updateproducts.length ; i++){
                                            await Product.update({_id:updateproducts[i].brandid},{$push:{Accreditation:updateproducts[i].Accreditation}});
                                        }
                                        console.log("Update "+updateproducts.length+" collection(s) in the database");
                                        res.render('successImport.pug',{numProducts:insertproducts.length, numUpdates: updateproducts.length});
                                    }
                                    else if(updateproducts.length == 0 && insertproducts.length == 0) {
                                        console.log("Nothing to update");
                                        res.render('successImport.pug',{numProducts:insertproducts.length, numUpdates: updateproducts.length});
                                    }

                                }
                            });
                            // res.send(products.length + " products have been successfully uploaded.");
                            // console.log(products);
                        });
                }
            }
        });
};

/* * * * * * * * * * * * * * * * * *
 * Communicate with the location API*
 * * * * * * * * * * * * * * * * * */

module.exports.getLocation = async function (req,res,next)
{
    var address = req.query.address;
    var addressfinder = "https://api.opencagedata.com/geocode/v1/json";
    var params = [
        "key=588cf2a44390452a8b410960e33fab66",
        "language=en",
        "format=json",
        "countrycode=au",
        "q="+ address,
        "no_annotations=1",
        "abbr=1"
    ]
    var url = addressfinder +"?"+params.join("&");
    var options = {
        url: url,
        Accept:"application/json",
        'Accept-Charset':'utf-8'
    }
    request(options,function(err,rest,data){
        if(err)
        {
            return next(err);
        }
        else
        {

            json = JSON.parse(data);
            var senddata = [];
            var sendgeo = []
            if(json.results.length > 0)
            {
                for(var i = 0 ; i < json.results.length ; i++)
                {
                    senddata.push(json.results[i].components);
                    sendgeo.push(json.results[i].geometry);
                }
                res.send({
                    matched:"true",
                    address: senddata,
                    geometry: sendgeo
                })
            }
            else
            {
                res.send({matched:"false"});
            }
        }
    })
}

/* * * * * * * * *
 * Wolfe's Code  *
 * * * * * * * * */

module.exports.logout = function(req, res, next)
{
    if (req.session)
    {
        // delete session object
        req.session.destroy(function (err)
        {
            if (err)
                return next(err);
            else
                return res.redirect('/');
        });
    }
};

module.exports.reset = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user)
        {
            if (errorUser)
            {
                return next(errorUser);
            }
            else
            {
                if (user === null)
                {
                    res.redirect('/');
                }
                else
                {
                    res.render('reset.pug');
                }
            }
        });
};

module.exports.forgot = function(req, res)
{
    res.render('forgotPwd.pug');
};

module.exports.forgotPwd = function(req, res, next)
{
    crypto.randomBytes(20, function (errResetToken, buf)
    {
        var token = buf.toString('hex');
        if(errResetToken)
        {
            res.send("Something wrong while creating reset token!");
        }
        else
        {
            User.findOne({ email: req.body.email }, function(err, user)
            {
                if (!user)
                {
                    return res.send('No account with that email address exists.');
                }

                user.resetPasswordToken = token;
                user.resetPasswordExpires = Date.now() + 3600000; // 1 hour

                user.save(function(errSave)
                {
                    if(errSave)
                    {
                        res.send("Something wrong while saving user's reset token and expire");
                    }
                    else
                    {
                        var smtpTransport = nodemailer.createTransport({
                            service: 'Gmail',
                            auth: {
                                user: 'kinder.foodfinder@gmail.com', //email address to send from
                                pass: '0466078428' //the actual password for that account
                            }
                        });

                        var mailOptions = {
                            to: user.email,
                            from: 'kinder.foodfinder@gmail.com',
                            subject: 'KinderFoodFinder Server Password Reset',
                            text: 'You are receiving this because you (or someone else) have requested the reset of the password for your account.\n\n' +
                                'Please click on the following link, or paste this into your browser to complete the process:\n\n' +
                                'http://' + req.headers.host + '/reset/token/' + token + '\n\n' +
                                'If you did not request this, please ignore this email and your password will remain unchanged.\n'
                        };

                        smtpTransport.sendMail(mailOptions, function(error, response)
                        {
                            if (error)
                            {
                                // console.log(error);
                                res.send(error);
                            }
                            else
                            {
                                // console.log(response);
                                res.send('An e-mail has been sent to ' + user.email + ' with further instructions.');
                            }
                            smtpTransport.close();
                        });
                    }
                });
            });
        }
    });
};

module.exports.resetPwd = function(req, res)
{
    User.findOne({resetPasswordToken: req.params.token, resetPasswordExpires: {$gt: Date.now()}}, function(err, user)
    {
        if(err)
        {
            res.send("Something wrong while finding the user with token!")
        }
        else if (!user)
        {
            res.send("Password reset token is invalid or has expired!");
        }
        else
        {
            res.render('resetPwd.pug');
        }
    });
};
module.exports.resetPwd2 = function(req, res)
{
    User.findOne({resetPasswordToken: req.params.token, resetPasswordExpires: {$gt: Date.now()}}, function(err, user)
    {
        if(err)
        {
            res.send("Something wrong while finding the user with token!")
        }
        else if (!user)
        {
            res.send("Password reset token is invalid or has expired!");
        }
        else
        {
            user.password = req.body.password;
            user.resetPasswordToken = undefined;
            user.resetPasswordExpires = undefined;

            user.save(function(errSave)
            {
                if(errSave)
                {
                    res.send("Something wrong while saving user's info!")
                }
                else
                {
                    var smtpTransport = nodemailer.createTransport({
                        service: 'Gmail',
                        auth: {
                            user: 'kinder.foodfinder@gmail.com', //email address to send from
                            pass: '0466078428' //the actual password for that account
                        }
                    });

                    var mailOptions = {
                        to: user.email,
                        from: 'kinder.foodfinder@gmail.com',
                        subject: 'Your password for KinderFoodFinder server has been changed!',
                        text: 'Hello,\n\n' +
                            'This is a confirmation that the password for your account ' + user.username + ' with email ' + user.email + ' has just been changed.\n'
                    };

                    smtpTransport.sendMail(mailOptions, function(error, response)
                    {
                        if (error)
                        {
                            // console.log(error);
                            res.send(error);
                        }
                        else
                        {
                            // console.log(response);
                            res.send('Success! Your password has been changed.');
                        }
                        smtpTransport.close();
                    });
                }
            });
        }
    });
};

module.exports.backFromSuccess = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user)
        {
            if (errorUser)
            {
                return next(errorUser);
            }
            else
            {
                if (user === null)
                {
                    res.redirect('/');
                }
                else
                {
                    res.redirect('/importinsertPage');
                }
            }
        });
};

module.exports.goToImportPage = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user)
        {
            if (errorUser)
            {
                return next(errorUser);
            }
            else
            {
                if (user === null)
                {
                    res.redirect('/');
                }
                else
                {
                    res.render('importInsertPage.pug');
                }
            }
        });
};

module.exports.goToReportPage = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user) {
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                } else {
                    var sortquery = req.query.sortquery;
                    var sortString = "";
                    var sortOrder = 1;
                    if(new String(sortquery).toLowerCase().valueOf() == new String("Brand1").toLowerCase().valueOf())
                    {
                        sortString = {
                            brandName : 1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("Brand1m").toLowerCase().valueOf())
                    {
                        sortString = {
                            brandName : -1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("StreetAdd1").toLowerCase().valueOf())
                    {
                        sortString = {
                            streetAddress : 1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("StreetAdd1m").toLowerCase().valueOf())
                    {
                        sortString = {
                            streetAddress : -1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("Store1").toLowerCase().valueOf())
                    {
                        sortString = {
                            storeName : 1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("Store1m").toLowerCase().valueOf())
                    {
                        sortString = {
                            storeName : -1
                        };
                    }
                    ReportedStore.find({}).sort(sortString)
                        .exec(function (errReport, reports) {
                            if (errReport) {
                                return next(errReport);
                            } else {
                                if (reports === null) {
                                    res.redirect('/');
                                } else {
                                    var perPage = (parseInt(req.query.perPage)) || 25;
                                    var page = (parseInt(req.query.page)) || 1;
                                    var displayreports = [];
                                    var reportsList = [];
                                    for (var i = 0; i < reports.length; i++) {
                                        if (req.query.searchstring == null) {
                                            reportsList.push(reports[i]);
                                        } else {
                                            if (reports[i].storeName.toLowerCase().includes(req.query.searchstring.toLowerCase())) {
                                                reportsList.push(reports[i]);
                                            }
                                        }
                                    }
                                    for (var i = ((perPage * page) - perPage); i < reportsList.length && i < (perPage * (page + 1) - perPage); i++) {
                                        displayreports.push(reportsList[i]);
                                    }
                                    res.render('report_dbmanagement.pug'
                                        , {
                                            displaydata: displayreports,
                                            numPerPage: perPage,
                                            count: reportsList.length,
                                            current: page,
                                            pages: Math.ceil(reportsList.length / perPage),
                                            countentries: displayreports.length,
                                            searchstring : req.query.searchstring||"",
                                            sortquery:req.query.sortquery
                                        }
                                    );
                                }
                            }
                        });
                }
            }
        })
};

module.exports.ReportPage_Delete = function(req,res,next)
{
    var rpids = req.query.rpids.split(',');
    User.findById(req.session.userId)
        .exec(function (errorUser, user) {
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                }
                else {
                    ReportedStore.remove({_id:{$in:rpids}})
                        .exec(function(errProduct)
                        {
                            if(errProduct)
                            {
                                return next(errProduct);
                            }
                            else
                            {
                                res.redirect('/report');
                            }
                        });
                }
            }
        })
};


// Insert new Store/ Address/ brand in store to the database
module.exports.ReportPage_Insert = async function(req,res,next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user) {
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                }
                else {
                    // Check if there are any identical store name
                    if(req.body.identicalStoreName=="true")
                    {
                        Store.findById(req.body.identicalStoreId)
                            .exec(function(errStore,store) {
                                if (store == null) {
                                    res.redirect('/');
                                } else {
                                var identicaladddress = false;
                                var identicaladddressid;
                                // Check whether they have the same address
                                for (var i = 0; i < store.Address.length; i++) {
                                    if (new String(req.body.address).toLowerCase().valueOf() == new String(store.Address[i].StreetAddress).toLowerCase().valueOf()) {
                                        identicaladddress = true;
                                        identicaladddressid = store.Address[i]._id;
                                    }
                                }
                                if (identicaladddress) {
                                    BrandinStore.find({storeid:req.body.identicalStoreId,addressid:identicaladddressid})
                                        .exec(function(errBrandinStore,BrandinStores)
                                        {
                                            if(errBrandinStore)
                                            {
                                                return next(errBrandinStore);
                                            }
                                            else
                                            {
                                                if(BrandinStores == null)
                                                {
                                                    var newbrandinstore = {
                                                        storeid: req.body.identicalStoreId,
                                                        brandid: req.body.brandid,
                                                        addressid: identicaladddressid
                                                    }
                                                    BrandinStore.create(newbrandinstore, async function (errBrandinStore, BrandinStore) {
                                                        if (errBrandinStore) {
                                                            return next(errBrandinStore);
                                                        } else {
                                                            if (BrandinStore == null) {
                                                                res.redirect('/');
                                                            } else {
                                                                ReportedStore.remove({_id:req.body.reportid})
                                                                    .exec(function(errReportedStore)
                                                                    {
                                                                        if(errReportedStore)
                                                                        {
                                                                            return next(errReportedStore);
                                                                        }
                                                                        else
                                                                        {
                                                                            res.redirect('/detailstorePage_Brand?storeid=' + req.body.identicalStoreId + "&addressid=" + newbrandinstore.addressid);
                                                                        }
                                                                    })
                                                            }
                                                        }
                                                    })
                                                }
                                                else
                                                {
                                                    var identicalbrand = false;
                                                    for (var i = 0 ; i < BrandinStores.length ; i++)
                                                    {
                                                        if(new String(BrandinStores[i].brandid).valueOf() == new String(req.body.brandid).valueOf())
                                                        {
                                                            identicalbrand = true;
                                                        }
                                                    }
                                                    if(identicalbrand)
                                                    {
                                                        ReportedStore.remove({_id:req.body.reportid})
                                                            .exec(function(errReportedStore)
                                                            {
                                                                if(errReportedStore)
                                                                {
                                                                    return next(errReportedStore);
                                                                }
                                                                else
                                                                {
                                                                    res.redirect('/detailstorePage_Brand?storeid=' + req.body.identicalStoreId + "&addressid=" + identicaladddressid);
                                                                }
                                                            })
                                                    }
                                                    else
                                                    {
                                                        var newbrandinstore = {
                                                            storeid: req.body.identicalStoreId,
                                                            brandid: req.body.brandid,
                                                            addressid: identicaladddressid
                                                        }
                                                        BrandinStore.create(newbrandinstore, async function (errBrandinStore, BrandinStore) {
                                                            if (errBrandinStore) {
                                                                return next(errBrandinStore);
                                                            } else {
                                                                if (BrandinStore == null) {
                                                                    res.redirect('/');
                                                                } else {
                                                                    ReportedStore.remove({_id:req.body.reportid})
                                                                        .exec(function(errReportedStore)
                                                                        {
                                                                            if(errReportedStore)
                                                                            {
                                                                                return next(errReportedStore);
                                                                            }
                                                                            else
                                                                            {
                                                                                res.redirect('/detailstorePage_Brand?storeid=' + req.body.identicalStoreId + "&addressid=" + newbrandinstore.addressid);
                                                                            }
                                                                        })
                                                                }
                                                            }
                                                        })
                                                    }
                                                }
                                            }
                                        })

                                } else {
                                    var newaddress = {
                                        _id: new mongoose.mongo.ObjectId(),
                                        StreetAddress: req.body.address,
                                        State: req.body.state,
                                        Postcode: req.body.postcode,
                                        Lat: req.body.lat,
                                        Long: req.body.long
                                    }
                                    Store.update({_id: req.body.identicalStoreId}, {$push: {Address: newaddress}})
                                        .exec(function (errStore) {
                                            if (errStore) {
                                                return next(errStore);
                                            } else {
                                                var newbrandinstore = {
                                                    storeid: req.body.identicalStoreId,
                                                    brandid: req.body.brandid,
                                                    addressid: newaddress._id
                                                }
                                                BrandinStore.create(newbrandinstore, async function (errBrandinStore, BrandinStore) {
                                                    if (errBrandinStore) {
                                                        return next(errBrandinStore);
                                                    } else {
                                                        if (BrandinStore == null) {
                                                            res.redirect('/');
                                                        } else {
                                                            ReportedStore.remove({_id:req.body.reportid})
                                                                .exec(function(errReportedStore)
                                                                {
                                                                    if(errReportedStore)
                                                                    {
                                                                        return next(errReportedStore);
                                                                    }
                                                                    else
                                                                    {
                                                                        res.redirect('/detailstorePage_Brand?storeid=' + req.body.identicalStoreId + "&addressid=" + BrandinStore.addressid);
                                                                    }
                                                                })


                                                        }
                                                    }
                                                })
                                            }
                                        })
                                }
                            }
                            })
                    }
                    else
                    {
                        var newstore = {
                            storeName: req.body.name,
                            Address: [],
                        }
                        var newaddress = {
                            StreetAddress: req.body.address,
                            Postcode: req.body.postcode,
                            State: req.body.state,
                            Lat: req.body.lat,
                            Long: req.body.long
                        }
                        newstore.Address.push(newaddress);
                        Store.create(newstore,async function(errStore,store)
                        {
                            if(errStore)
                            {
                                var err = new Error('Something wrong when insert new store!');
                                err.status = 400;
                                return res.send(err);
                            }
                            else
                            {
                                var newbrandinstore = {
                                    storeid: store._id,
                                    brandid: req.body.brandid,
                                    addressid: store.Address[0]._id
                                }
                                BrandinStore.create(newbrandinstore, async function(errBrandinStore,BrandinStore)
                                {
                                    if(errBrandinStore)
                                    {
                                        return next(errBrandinStore);
                                    }
                                    else
                                    {
                                        if(BrandinStore == null)
                                        {
                                            res.redirect('/');
                                        }
                                        else
                                        {
                                            ReportedStore.remove({_id:req.body.reportid})
                                                .exec(function(errReportedStore)
                                                {
                                                    if(errReportedStore)
                                                    {
                                                        return next(errReportedStore);
                                                    }
                                                    else
                                                    {
                                                        res.redirect('/detailstorePage_Brand?storeid='+newbrandinstore.storeid+"&addressid="+newbrandinstore.addressid);
                                                    }
                                                })
                                        }
                                    }
                                })
                            }
                        })
                    }
                }
            }
        })
}

module.exports.goToPublishPage = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user)
        {
            if (errorUser)
            {
                return next(errorUser);
            }
            else
            {
                if (user === null)
                {
                    res.redirect('/');
                }
                else
                {
                    Version.find({}, function(errorFind, versions)
                    {
                        if(errorFind)
                        {
                            res.send("Something wrong while finding versions!")
                        }
                        else
                        {
                            res.render('publish.pug', {versions: versions});
                        }
                    });
                }
            }
        });
};

module.exports.publishBrandData = function(req, res, next)
{
    Version.findOne({type: "brand"}, function(errorFind, version)
    {
       if(errorFind)
       {
           res.send("Something went wrong while searching the version for brand!");
       }
       else
       {
            var updateVersion = parseInt(version.version) + 1;
            Version.updateOne({type: "brand"}, {version: updateVersion.toString()}, function(errorUpdate, version)
            {
                if(errorUpdate)
                {
                    res.send("Something went wrong while updating the version for brand!");
                }
                else
                {
                    res.redirect('/publish');
                }
            });
       }
    });
};

module.exports.publishStoreData = function(req, res, next)
{
    Version.findOne({type: "store"}, function(errorFind, version)
    {
        if(errorFind)
        {
            res.send("Something went wrong while searching the version for store!");
        }
        else
        {
            var updateVersion = parseInt(version.version) + 1;
            Version.updateOne({type: "store"}, {version: updateVersion.toString()}, function(errorUpdate, version)
            {
                if(errorUpdate)
                {
                    res.send("Something went wrong while updating the version for store!");
                }
                else
                {
                    res.redirect('/publish');
                }
            });
        }
    });
};

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Product Detail Page which enables the product to be updated *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/** Direct user to the product detail page.
 * Hence, the product page that will be displayed depends on the productid that is passed to this module
 */
module.exports.goToProductDetailPage = function (req,res,next)
{
        //Search in the database for the brand that have the exact id with with the productid from req.query
        Product.findById(req.query.productid)
        .exec(function(errorProduct, product)
        {
            if (errorProduct)
            {
                return next(errorProduct);
            }
            else
            {
                if(product === null)
                {
                    res.redirect('/');
                }
                else
                {
                    // Search for stores and addresses that have this brand.
                    // This method is called to display the number of stores and addresses
                    // that have this brand in the interface
                    BrandinStore.find({brandid:req.query.productid})
                        .exec(function(errBrandinStore, brandinstore)
                        {
                            if(errBrandinStore)
                            {
                                return next(errBrandinStore);
                            }
                            else
                            {
                                // Conditional statement to check whether a brand has image or not
                                if(product.Image == null || product.Image.localeCompare("")==0){
                                    res.render('productDetailPage/productDetailPage.pug',{
                                        brandid: product._id,
                                        brandname : product.Brand_Name,
                                        brandcategory: product.Category,
                                        brandimage: "null",
                                        accreditacount: product.Accreditation.length,
                                        storecount:brandinstore.length
                                    });
                                }
                                else {
                                    res.render('productDetailPage/productDetailPage.pug',{
                                        brandid: product._id,
                                        brandname : product.Brand_Name,
                                        brandcategory: product.Category,
                                        brandimage: product.Image,
                                        accreditacount: product.Accreditation.length,
                                        storecount:brandinstore.length
                                    });
                                }
                            }
                        })
                }
            }
        });
}

/** Update brand's summary information: Name, Category, Image, */
module.exports.ProductDetailPage_updateBrandSummary = async function(req,res,next)
{
    var Brand_Name = req.body.brand_name;
    var Brand_Category = req.body.brand_category;
    var Brand_Id = req.body.brand_id;

    Product.findById(Brand_Id)
        .exec(function(errorBrand, brand)
        {
            if(errorBrand)
            {
                return next(errorBrand);
            }
            else
            {
                if(req.files !== null)
                {
                    Product.update({_id:Brand_Id},{$set:{Brand_Name:Brand_Name, Category:Brand_Category,Image:Brand_Id}})
                        .exec(function(errorUpdate){
                            if(errorUpdate){
                                return next(errorUpdate);
                            }
                            else
                            {
                                var image = req.files.Image;
                                var buf = new Buffer(image.data, 'base64');
                                fs.writeFile('public/uploads/'+Brand_Id+".jpg",buf,function(err){
                                    if(err)
                                    {
                                        return next(err);
                                    }
                                    else
                                    {
                                        res.redirect('/detailproductPage?productid=' + Brand_Id);
                                    }
                                })
                            }
                        });
                }
                else
                {
                    Product.update({_id:Brand_Id},{$set:{Brand_Name:Brand_Name, Category:Brand_Category}})
                        .exec(function(errorUpdate){
                            if(errorUpdate){
                                return next(errorUpdate);
                            }
                            else
                            {
                                res.redirect('/detailproductPage?productid=' + Brand_Id);
                            }
                        });
                }

            }
        });
};


/** Display all accreditations available for a product,
 * This view also enables the admin to edit an accreditation of a brand or add more accreditation(s) to the brand
 */
module.exports.ProductDetailPage_Accreditation = async function(req,res,next){
    Product.findById(req.query.productid)
        .exec(function(errProduct,product)
        {
            if(errProduct)
            {
                return next(errProduct);
            }
            else
            {
                if(product === null)
                {
                    res.redirect('/');
                }
                else
                {
                    var perPage = 25;
                    var page = (parseInt(req.query.page)) || 1;
                    var displayaccreditation = [];
                    var accreditationList = [];
                    for(var i = 0 ; i < product.Accreditation.length ; i++)
                    {
                        if(req.query.searchstring == null)
                        {
                            accreditationList.push(product.Accreditation[i]);
                        }
                        else
                        {
                            if(product.Accreditation[i].Accreditation.toLowerCase().includes(req.query.searchstring.toLowerCase()))
                            {
                                accreditationList.push(product.Accreditation[i]);
                            }
                        }
                    }
                    for( var i = ((perPage * page) - perPage) ; i < accreditationList.length && i < (perPage * (page+1) - perPage) ; i++)
                    {
                        displayaccreditation.push(accreditationList[i]);
                    }
                    var searchstring = req.query.searchstring;
                    res.render('productDetailPage/productDetailPage_Accreditation.pug'
                        ,{
                        brandid: product._id,
                        searchstring:searchstring,
                        current:page,
                        pages:Math.ceil(accreditationList.length/ perPage),
                        brandname : product.Brand_Name,
                        brandcategory: product.Category,
                        accreditation: displayaccreditation
                    }
                    );
                }
            }
        });
}

/** Delete accreditations in a particular Brand
 */
module.exports.ProductDetailPage_Accreditation__Delete = async function(req,res,next){
    var accrids = req.query.accrids.split(',');
    Product.update({_id:req.query.productid},{$pull:{Accreditation:{_id:{$in:accrids}}}})
        .exec(function(errProduct)
        {
            if(errProduct)
            {
                return next(errProduct);
            }
            else
            {
                res.redirect('/detailproductPage_Accreditation?productid='+req.query.productid);
            }
        });
}

/** Update on accreditation in a particular brand
 */

module.exports.ProductDetailPage_Accreditation__Update = async function(req,res,next){
    Product.update(
        { 'Accreditation._id': req.query.accrid,'_id':req.query.productid},
        { $set:  { 'Accreditation.$.Accreditation': req.query.accreditation,'Accreditation.$.Rating': req.query.rating }})
        .exec(function(errProduct)
        {
            if(errProduct)
            {
                return next (errProduct);
            }
            else
            {
                res.redirect('/detailproductPage_Accreditation?productid='+req.query.productid);
            }
        })
}

/** Insert new Accreditation to a particular brand
 */

module.exports.ProductDetailPage_Accreditation__Insert = async function(req,res,next){
    var newAccreditation = {
        Accreditation:req.query.accreditation,
        Rating:req.query.rating,
        _id: new mongoose.mongo.ObjectId()
    }
    Product.update(
        { '_id':req.query.productid},
        { $push:  { 'Accreditation': newAccreditation }})
        .exec(function(errProduct)
        {
            if(errProduct)
            {
                return next (errProduct);
            }
            else
            {
                res.redirect('/detailproductPage_Accreditation?productid='+req.query.productid);
            }
        })
}

/** Display all stores that have this product,
 * This view also enables the admin to add new stores (That are already in the database),
 * or remove this brand from a store
 */
module.exports.ProductDetailPage_Store = async function(req,res,next){
    Product.findById(req.query.productid)
        .exec(function(errProduct,product)
        {
            if(errProduct)
            {
                return next(errProduct);
            }
            else
            {
                if(product === null)
                {
                    res.redirect('/');
                }
                else
                {
                    // Conduct search for stores and addresses that have this brand.
                    // The stores and addresses will be displayed in the productDetailPage_Store view
                    BrandinStore.find({brandid:req.query.productid})
                        .exec(function(errBrandinStore,BrandinStore){
                            if(errBrandinStore)
                            {
                                return next(errProduct);
                            }
                            else
                            {
                                // Process the stores and addresses that will be displayed in the view.
                                // The number of stores and addresses per page is limited to on 25.
                                // Likewise, the admin can use the pagination buttons in the view to move between pages (Which will be reflected in req.query.page
                                var storeidlist = [];
                                for(var i = 0; i < BrandinStore.length ; i++)
                                {
                                    storeidlist.push(BrandinStore[i].storeid);
                                }
                                Store.find({_id:{$in:storeidlist}})
                                    .exec(function(errStore,stores)
                                    {
                                        if(errStore)
                                        {
                                            return next(errStore);
                                        }
                                        else
                                        {
                                            var perPage = 25;
                                            var page = (parseInt(req.query.page)) || 1;
                                            var displaystore = [];
                                            var storeList = [];
                                            for(var i = 0 ; i < stores.length ; i++)
                                            {
                                                if(req.query.searchstring == null)
                                                {
                                                    for(var j = 0; j < stores[i].Address.length ; j++)
                                                    {
                                                        for(var k = 0; k < BrandinStore.length ; k++)
                                                        {
                                                            if(new String(stores[i].Address[j]._id).valueOf() == new String(BrandinStore[k].addressid).valueOf())
                                                            {
                                                                var storeaddress = {
                                                                    storeaddressid: BrandinStore[k]._id,
                                                                    storeName: stores[i].storeName,
                                                                    storeAddress: stores[i].Address[j].StreetAddress + ", " + stores[i].Address[j].Postcode + ", " + stores[i].Address[j].State
                                                                }
                                                                storeList.push(storeaddress);
                                                            }
                                                        }
                                                    }

                                                }
                                                else
                                                {
                                                    for(var j = 0; j < stores[i].Address.length ; j++)
                                                    {
                                                        for(var k = 0; k < BrandinStore.length ; k++)
                                                        {
                                                            if(new String(stores[i].Address[j]._id).valueOf() == new String(BrandinStore[k].addressid).valueOf())
                                                            {
                                                                if(stores[i].storeName.toLowerCase().includes(req.query.searchstring.toLowerCase()))
                                                                {
                                                                    var storeaddress = {
                                                                        storeaddressid: BrandinStore[k]._id,
                                                                        storeName: stores[i].storeName,
                                                                        storeAddress: stores[i].Address[j].StreetAddress + ", " + stores[i].Address[j].Postcode + ", " + stores[i].Address[j].State
                                                                };
                                                                    storeList.push(storeaddress);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            for( var i = ((perPage * page) - perPage) ; i < storeList.length && i < (perPage * (page+1) - perPage) ; i++)
                                            {
                                                displaystore.push(storeList[i]);
                                            }
                                            var searchstring = req.query.searchstring;
                                            res.render('productDetailPage/productDetailPage_Store.pug'
                                                ,{
                                                    brandid: product._id,
                                                    searchstring:searchstring,
                                                    current:page,
                                                    pages:Math.ceil(storeList.length/ perPage),
                                                    brandname : product.Brand_Name,
                                                    brandcategory: product.Category,
                                                    storeaddress: displaystore
                                                }
                                            );
                                        }
                                    })
                            }
                        })
                }
            }
        });
}


/** Delete the connection of a brand with the store
 */
module.exports.ProductDetailPage_Store__Delete = async function(req,res,next){
    var brinstids = req.query.addids.split(',');
    BrandinStore.remove({_id:{$in:brinstids}})
        .exec(function(errBrandinStore)
        {
            if(errBrandinStore)
            {
                return next(errBrandinStore);
            }
            else
            {
                res.redirect('/detailproductPage_Store?productid='+req.query.productid);
            }
        })
}

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Store Detail Page which enables the store to be udpated *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

module.exports.goToStoreDetailPage = async function(req,res,next)
{
    Store.findById(req.query.storeid)
        .exec(function(errorStore,store)
        {
          if(errorStore)
          {
              return next(errorStore);
          }
          else
          {
              if(store == null)
              {
                  res.redirect('/');
              }
              else
              {
                  BrandinStore.find({storeid:req.query.storeid})
                      .exec(function(errBrandinStore,BrandinStore)
                      {
                          if(errBrandinStore)
                          {
                              return next(errBrandinStore);
                          }
                          else
                          {
                              var numberofbrand = [];
                              var count = 0;
                              for(var i = 0 ; i < BrandinStore.length ; i ++)
                              {
                                  var identical = false;
                                  for(var j = 0 ; j < numberofbrand.length ; j ++)
                                  {
                                      if(BrandinStore[i].brandid == numberofbrand[j])
                                      {
                                          identical = true;
                                      }
                                  }
                                  if(!identical)
                                  {
                                      numberofbrand.push(BrandinStore[j].brandid);
                                  }
                              }
                              res.render('storeDetailPage/storeDetailPage.pug', {
                                  storeid: store._id,
                                  storename: store.storeName,
                                  addressnumb: store.Address.length,
                                  countnumberofbrand: numberofbrand.length
                              })
                          }
                      })

              }
          }
        })
}

/**
 * Function to update a Store summary (Name of a store)
 */

module.exports.StoreDetailPage_updateStoreSummary = async function(req,res,next)
{
    var Store_Name = req.body.store_name;
    var Store_Id = req.body.store_id;

    Store.update({_id:Store_Id},{$set:{storeName: Store_Name}})
        .exec(function(errorStore) {
            if (errorStore) {
                return next(errorStore);
            } else {
                res.redirect('/detailstorePage?storeid=' + Store_Id);
            }
        });
}

/** Display all Addresses available for a store
 * This view also enables the admin to delete and/ or add addresses to the store
 */


module.exports.StoreDetailPage_Address = async function(req,res,next)
{
    Store.findById(req.query.storeid)
        .exec(function(errStore,store)
        {
            if(errStore)
            {
                return next(errStore);
            }
            else
            {
                if(store == null)
                {
                    res.redirect('/');
                }
                else
                {
                    var searchstring = req.query.searchstring;
                    var perPage = 25;
                    var page = (parseInt(req.query.page)) || 1;
                    var addresslist = [];
                    var displayaddresslist = [];

                    for(var i = 0 ; i < store.Address.length ; i ++)
                    {
                        if (req.query.searchstring == null) {
                            addresslist.push(store.Address[i]);
                        } else {
                            if (store.Address[i].StreetAddress.toLowerCase().includes(req.query.searchstring.toLowerCase())) {
                                addresslist.push(store.Address[i]);
                            }
                        }
                    }
                    for (var i = ((perPage * page) - perPage); i < addresslist.length && i < (perPage * (page + 1) - perPage); i++) {
                        displayaddresslist.push(addresslist[i]);
                    }
                    var displayaddresslistids = []
                    for (var i = 0 ; i < displayaddresslist.length ; i++)
                    {
                        displayaddresslistids.push(displayaddresslist[i]._id);
                    }
                    BrandinStore.find({addressid:{$in:displayaddresslistids}})
                        .exec(function(errBrandinStore,BrandinStore)
                        {
                            if(errBrandinStore)
                            {
                                return next(errBrandinStore);
                            }
                            else
                            {
                                var countnumberofbrand = [];
                                for( var i = 0 ; i < displayaddresslist.length ; i++)
                                {
                                    var count = 0;

                                    for( var j = 0 ; j< BrandinStore.length ; j ++)
                                    {
                                        if(displayaddresslist[i]._id == BrandinStore[j].addressid)
                                        {
                                            count++;
                                        }
                                    }
                                    countnumberofbrand.push(count);
                                }
                                res.render('storeDetailPage/storeDetailPage_Address.pug'
                                    , {
                                        storeid: store._id,
                                        storename: store.storeName,
                                        searchstring: searchstring,
                                        current: page,
                                        pages: Math.ceil(addresslist.length / perPage),
                                        address: displayaddresslist,
                                        countnumberofbrand:countnumberofbrand
                                    });
                            }
                        })
                }
            }
        })
}

/** Delete addresses in a particular store
 */
module.exports.StoreDetailPage_Address__Delete = async function(req,res,next){
    var addids = req.query.addids.split(',');
    Store.update({_id:req.query.storeid},{$pull:{Address:{_id:{$in:addids}}}})
        .exec(function(errStore)
        {
            if(errStore)
            {
                return next(errStore);
            }
            else
            {
                BrandinStore.remove({addressid:{$in:addids}})
                    .exec(function(errBrandinStore){
                        if(errBrandinStore)
                        {
                            return next(errBrandinStore)
                        }
                        else
                        {
                            res.redirect('/detailstorePage_Address?storeid='+req.query.storeid);
                        }
                    })
            }
        });
}

/**
 * Insert address to a particular store
 */

module.exports.StoreDetailPage_Address__Insert = async function(req,res,next)
{
    var newaddress = {
        StreetAddress: req.body.address,
        State:req.body.state,
        Postcode: req.body.postcode,
        Lat: req.body.lat,
        Long: req.body.long
    }
    Store.update({_id:req.body.storeid},{$push:{Address:newaddress}})
        .exec(function(errStore)
        {
            if(errStore)
            {
                return next(errStore);
            }
            else
            {
                res.redirect('/detailstorePage_Address?storeid='+req.body.storeid);
            }
        })
}

module.exports.StoreDetailPage_Address__FindBrandNotInthis = function(req,res,next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user) {
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                } else {
                    BrandinStore.find({'addressid':req.query.addressid})
                        .exec(function(errBrandinStore,BrandinStore)
                        {
                            if(errBrandinStore)
                            {
                                return next(errBrandinStore)
                            }
                            else {
                                if(BrandinStore == null)
                                {
                                    res.redireict('/');
                                }
                                else
                                {
                                    var productlist = [];
                                    for(var i = 0 ; i< BrandinStore.length ; i ++){
                                        productlist.push(BrandinStore[i].brandid)
                                    }
                                    Product.find({ "_id": { "$nin": productlist } })
                                        .exec(function(errProduct,products)
                                        {
                                            if(errProduct)
                                            {
                                                return next(errProduct);
                                            }
                                            else
                                            {
                                                if(products == null)
                                                {
                                                    res.redirect('/')
                                                }
                                                else {
                                                    res.json(products);
                                                }
                                            }
                                        })
                                }
                            }
                        })
                }
            }
        })
}


/** Display all Brands available for a store,
 * This view also enables the admin to delete and/ or add brands to the store
 */

module.exports.StoreDetailPage_Brand = async function(req,res,next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user) {
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                } else {
                    BrandinStore.find({addressid: req.query.addressid,storeid:req.query.storeid})
                        .exec(function(errBrandinStore,brandinstoreinfo)
                        {
                            if(errBrandinStore)
                            {
                                return next(errBrandinStore);
                            }
                            else
                            {
                                var brand = [];
                                for (var i = 0; i < brandinstoreinfo.length; i++) {
                                    brand.push(brandinstoreinfo[i].brandid);
                                }
                                Product.find()
                                    .exec(async function(errProduct,products)
                                    {
                                        if(errProduct)
                                        {
                                            return next(errProduct);
                                        }
                                        else
                                        {
                                            if(products == null)
                                            {
                                                res.redirect('/')
                                            }
                                            else
                                            {
                                                var perPage = 25;
                                                var page = req.query.page || 1;
                                                var displaybrandlist = [];
                                                var brandnotinstore = [];
                                                var brandinstore = [];
                                                for(var i = 0 ; i < products.length ; i++)
                                                {
                                                    var identical = false;
                                                    for( var j = 0 ; j < brand.length ; j++)
                                                    {
                                                        if(new String(products[i]._id).valueOf() == new String(brand[j]).valueOf())
                                                        {
                                                            identical = true;
                                                        }
                                                    }
                                                    if(identical)
                                                    {
                                                        brandinstore.push(products[i]);
                                                    }
                                                    else
                                                    {
                                                        brandnotinstore.push(products[i]);
                                                    }
                                                }
                                                var searchstring = req.query.searchstring;
                                                for (var i = ((perPage * page) - perPage); i < brandinstore.length && i < (perPage * (page + 1) - perPage); i++) {
                                                    if(searchstring !== undefined)
                                                    {
                                                        if(new String(brandinstore[i].Brand_Name.toLowerCase()).valueOf().includes(new String(searchstring).toLowerCase().valueOf()))
                                                        {
                                                            displaybrandlist.push(brandinstore[i]);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        displaybrandlist.push(brandinstore[i]);
                                                    }

                                                }
                                                var displaybrandinstoreinfoid = [];
                                                for( var i = 0 ; i < displaybrandlist.length; i ++)
                                                {
                                                    for( var j = 0 ; j < brandinstoreinfo.length ; j ++)
                                                    {
                                                        if(new String(brandinstoreinfo[j].brandid).valueOf() == new String(displaybrandlist[i]._id).valueOf())
                                                        {
                                                            displaybrandinstoreinfoid.push(brandinstoreinfo[j]._id);
                                                        }
                                                    }
                                                }
                                                Store.findById(req.query.storeid)
                                                    .exec(function(errStore,store)
                                                    {
                                                        if(errStore)
                                                        {
                                                            return next(errStore);
                                                        }
                                                        else
                                                        {
                                                            if(store == null)
                                                            {
                                                                res.redirect('/');
                                                            }
                                                            else
                                                            {
                                                                var displayaddress;
                                                                for(var i = 0; i < store.Address.length ; i++)
                                                                {
                                                                    if(store.Address[i]._id == req.query.addressid)
                                                                    {
                                                                        displayaddress = store.Address[i];
                                                                    }
                                                                }
                                                                res.render('storeDetailPage/storeDetailPage_Brand.pug'
                                                                    , {
                                                                        storeid: req.query.storeid,
                                                                        addressid: req.query.addressid,
                                                                        searchstring: searchstring,
                                                                        current: page,
                                                                        displaybrandinstoreid:displaybrandinstoreinfoid,
                                                                        pages: Math.ceil(brandinstore.length / perPage),
                                                                        storename: store.storeName,
                                                                        displayaddress: displayaddress,
                                                                        brand: displaybrandlist,
                                                                        brandnotinstore:brandnotinstore
                                                                    });
                                                            }
                                                        }
                                                    })
                                            }
                                        }
                                    })
                            }
                        })

                }
            }
    })
}

/** Delete brands in a particular store
 */
module.exports.StoreDetailPage_Brand__Delete = async function(req,res,next){
    var brinstids = req.query.brinstids.split(',');
    BrandinStore.remove({_id:{$in:brinstids}})
        .exec(function(errBrandinStore)
        {
            if(errBrandinStore)
            {
                return next(errBrandinStore);
            }
            else
            {
                res.redirect('/detailstorePage_Brand?storeid='+req.query.storeid+"&addressid="+req.query.addressid);
            }
        })
}

module.exports.StoreDetailPage_Brand__Insert = async function(req,res,next)
{
    var productinstore = {
        storeid: req.query.storeid,
        brandid: req.query.brandid,
        addressid: req.query.addressid
    }
    BrandinStore.find({"brandid":req.query.brandid})
        .exec(function (errorBrandinStore, brandinstore) {
            if (errorBrandinStore) {
                return next(errorBrandinStore);
            } else {
                if (brandinstore === null) {
                    BrandinStore.create(productinstore,async function(errStore,BrandinStore)
                    {
                        if(errStore)
                        {
                            res.json({
                                matched:"Some errors have occurred when insert new brand to the store"
                            });
                        }
                        else
                        {
                            res.json({
                                matched:"Successfully insert new brand to the store"
                            });
                        }
                    })

                } else {
                    var alreadyinstore = false;
                    for(var i = 0 ; i < brandinstore.length ; i++)
                    {
                        if(brandinstore[i].brandid == req.query.brandid && brandinstore[i].storeid == req.query.storeid && brandinstore[i].addressid == req.query.addressid)
                        {
                            alreadyinstore = true;
                        }
                    }
                    if(alreadyinstore){
                        res.json({
                            matched:"This store already has the brand"
                        });
                    }
                    else
                    {
                        BrandinStore.create(productinstore,async function(errStore,store)
                        {
                            if(errStore)
                            {
                                var err = new Error('Something wrong when insert new information!');
                                err.status = 400;
                                return res.send(err);
                            }
                            else
                            {
                                res.json({
                                    matched:"Successfully insert new brand to the store"
                                });
                            }
                        })
                    }
                }
            }
        })
}

/** Store databasemanagement function
 */

module.exports.StoredatabaseManagement = function(req,res,next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user) {
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                } else {
                    var sortquery = req.query.sortquery;
                    var sortString = "";
                    var sortOrder = 1;
                    if (new String(sortquery).toLowerCase().valueOf() == new String("Store1").toLowerCase().valueOf()) {
                        sortString = {
                            "$sort": {storeName: 1}
                        };
                    } else if (new String(sortquery).toLowerCase().valueOf() == new String("Store1m").toLowerCase().valueOf()) {
                        sortString = {
                            "$sort": {storeName: -1}
                        };
                    } else if (new String(sortquery).toLowerCase().valueOf() == new String("Address1").toLowerCase().valueOf()) {
                        sortString = {
                            "$sort": {Address: 1}
                        };
                    } else if (new String(sortquery).toLowerCase().valueOf() == new String("Address1m").toLowerCase().valueOf()) {
                        sortString = {
                            "$sort": {Address: -1}
                        };
                    } else if (new String(sortquery).toLowerCase().valueOf() == new String("BrandCount1").toLowerCase().valueOf()) {
                        sortString = {
                            "$sort": {BrandCount: -1}
                        };
                    } else if (new String(sortquery).toLowerCase().valueOf() == new String("BrandCount1m").toLowerCase().valueOf()) {
                        sortString = {
                            "$sort": {BrandCount: 1}
                        };
                    }
                    var projection = [{
                        "$project": {
                            "storeName": 1,
                            "Address": 1,
                        }
                    }];
                    if (sortString !== "") {
                        projection.push(sortString);
                    }
                    Store.aggregate(projection,
                        function (errStore, stores) {
                            if (errStore) {
                                return next(errStore);
                            } else {
                                if (stores === null) {
                                    res.redirect('/');
                                } else {
                                    var perPage = (parseInt(req.query.perPage)) || 25;
                                    var page = (parseInt(req.query.page)) || 1;
                                    var displaystores = [];
                                    var storesList = [];
                                    for (var i = 0; i < stores.length; i++) {
                                        if (req.query.searchstring == null) {
                                            storesList.push(stores[i]);
                                        } else {
                                            if (stores[i].storeName.toLowerCase().includes(req.query.searchstring.toLowerCase())) {
                                                storesList.push(stores[i]);
                                            }
                                        }
                                    }
                                    for (var i = ((perPage * page) - perPage); i < storesList.length && i < (perPage * (page + 1) - perPage); i++) {
                                        displaystores.push(storesList[i]);
                                    }
                                    var displaystoresid = [];
                                    for( var i = 0 ; i < displaystores.length ; i++)
                                    {
                                        displaystoresid.push(displaystores[i]._id);
                                    }
                                    var searchstring = req.query.searchstring;
                                    BrandinStore.find({storeid:{$in:displaystoresid}})
                                        .exec(function(errBrandinStore,BrandinStore)
                                        {
                                            if(errBrandinStore)
                                            {
                                                return next(errBrandinStore);
                                            }
                                            else
                                            {
                                                var numberofbrand = [];
                                                var countnumberofbrand = [];
                                                for( var i = 0 ; i < displaystores.length ; i++)
                                                {
                                                    var count = 0;
                                                    for(var j = 0 ; j < BrandinStore.length ; j ++)
                                                    {
                                                        if(displaystores[i]._id == BrandinStore[j].storeid)
                                                        {
                                                            var identical = false;
                                                            for(var k = 0 ; k < numberofbrand.length ; k ++)
                                                            {
                                                                if(BrandinStore[j].brandid == numberofbrand[k].brandid && BrandinStore[j].storeid == numberofbrand[k].storeid)
                                                                {
                                                                    identical = true;
                                                                }
                                                            }
                                                            if(!identical)
                                                            {
                                                                numberofbrand.push(BrandinStore[j]);
                                                                count++;
                                                            }
                                                        }

                                                    }
                                                    countnumberofbrand.push(count);
                                                }
                                                res.render('store_dbmanagement.pug'
                                                    , {
                                                        displaydata: displaystores,
                                                        numPerPage: perPage,
                                                        count: storesList.length,
                                                        current: page,
                                                        countnumberofbrand: countnumberofbrand,
                                                        pages: Math.ceil(storesList.length / perPage),
                                                        countentries: displaystores.length,
                                                        searchstring: req.query.searchstring || "",
                                                        sortquery: req.query.sortquery
                                                    }
                                                );
                                            }
                                        })

                                }
                            }
                        })
                }
            }
        })
};
module.exports.StoredatabaseManagement_Delete = function(req,res,next)
{
    var stids = req.query.stids.split(',');
    User.findById(req.session.userId)
        .exec(function(errorUser,user){
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                } else {
                    Store.remove({_id:{$in:stids}})
                        .exec(function(errStore)
                        {
                            if(errStore)
                            {
                                return next(errStore);
                            }
                            else
                            {
                                BrandinStore.remove({storeid:{$in:stids}})
                                    .exec(function(errBrandinStore){
                                        if(errBrandinStore)
                                        {
                                            return next(errBrandinStore)
                                        }
                                        else
                                        {
                                            res.redirect('/store_dbmanagement');
                                        }
                                    })
                            }
                        });
                }
            }
        })
}


module.exports.databaseManagement = function(req, res, next)
{
    User.findById(req.session.userId)
        .exec(function (errorUser, user) {
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                } else {
                    var sortquery = req.query.sortquery;
                    var sortString = "";
                    var sortOrder = 1;
                    if(new String(sortquery).toLowerCase().valueOf() == new String("Brand1").toLowerCase().valueOf())
                    {
                        sortString = {
                            Brand_Name : 1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("Brand1m").toLowerCase().valueOf())
                    {
                        sortString = {
                            Brand_Name : -1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("Category1").toLowerCase().valueOf())
                    {
                        sortString = {
                            Category : 1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("Category1m").toLowerCase().valueOf())
                    {
                        sortString = {
                            Category : -1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("Accreditation1").toLowerCase().valueOf())
                    {
                        sortString = {
                            Accreditation : 1
                        };
                    }
                    else if(new String(sortquery).toLowerCase().valueOf() == new String("Accreditation1m").toLowerCase().valueOf())
                    {
                        sortString = {
                            Accreditation : -1
                        };
                    }
                    Product.find({}).sort(sortString)
                        .exec(function (errProduct, products) {
                            if (errProduct) {
                                return next(errProduct);
                            } else {
                                if (products === null) {
                                    res.redirect('/');
                                } else {
                                    var perPage = (parseInt(req.query.perPage)) || 25;
                                    var page = (parseInt(req.query.page)) || 1;
                                    var displayproducts = [];
                                    var productsList = [];
                                    for (var i = 0; i < products.length; i++) {
                                        if (req.query.searchstring == null) {
                                            productsList.push(products[i]);
                                        } else {
                                            if (products[i].Brand_Name.toLowerCase().includes(req.query.searchstring.toLowerCase())) {
                                                productsList.push(products[i]);
                                            }
                                        }
                                    }
                                    for (var i = ((perPage * page) - perPage); i < productsList.length && i < (perPage * (page + 1) - perPage); i++) {
                                        displayproducts.push(productsList[i]);
                                    }
                                    var searchstring = req.query.searchstring;
                                    res.render('dbmanagement.pug'
                                        , {
                                            displaydata: displayproducts,
                                            numPerPage: perPage,
                                            count: productsList.length,
                                            current: page,
                                            pages: Math.ceil(productsList.length / perPage),
                                            countentries: displayproducts.length,
                                            searchstring : req.query.searchstring||"",
                                            sortquery:req.query.sortquery
                                        }
                                    );
                                }
                            }
                        });
                }
            }
        })
};


module.exports.databaseManagement_Delete = function(req,res,next)
{
    var brids = req.query.brids.split(',');

    Product.remove({_id:{$in:brids}})
        .exec(function(errProduct)
        {
            if(errProduct)
            {
                return next(errProduct);
            }
            else
            {
                BrandinStore.remove({brandid:{$in:brids}})
                    .exec(function(errBrandinStore){
                        if(errBrandinStore)
                        {
                            return next(errBrandinStore)
                        }
                        else
                        {
                            res.redirect('/dbmanagement');
                        }
                    })
            }
        });
};

/* * * * * * * * * * * * * * * * * *
 * Communication with Android app  *
 * * * * * * * * * * * * * * * * * */

// only for testing
// var http = require("http");
//
// http.createServer(function(request, response)
// {
//     var data;
//     request.addListener("data", function(postDataChunk)
//     {
//         data += postDataChunk;
//     });
//
//     request.addListener("end", function()
//     {
//         console.log('KFF app listening on port 8888!');
//         console.log("data: " + data);
//     });
// }).listen(8888);

module.exports.registerAndroidAppUsers = function(req, res, next)
{
    // testing
    var name = req.query.name;
    console.log(name);

    // set up and receive the user info
    var appUserData = {
        name: req.query.name,
        gender: req.query.gender,
        email: req.query.email,
        password: req.query.password,
        birthday: req.query.birthday
    };

    // create the user account
    AppUser.create(appUserData, function (error, appUser)
    {
        if (error)
        {
            var err = new Error('User info is invalid!');
            err.status = 400;
            return next(err);
        }
        else
        {
            res.send("Create," + appUser._id.toString());
        }
    });
};

module.exports.loginAndroidAppUsers = function(req, res, next)
{
    // set up and receive the user info
    var email = req.query.email;
    var password = req.query.password;
    console.log(email);

    AppUser.authenticate(email, password, function (error, user)
    {
        if (error)
        {
            res.send("Error");
        }
        else if(!user)
        {
            res.send("No");
        }
        else
        {
            res.send("Yes," + user.gender + "," + user.birthday + "," + user._id.toString() + "," + user.name + "," + user.email);
        }
    });
};

// to test around this:
// http://<Server's IP>:3000/android-app-report-store?storeName=coles&streetAddress=broadway&state=NSW&postCode=2007&productId=5ccd6ad2334d7711a0ff5c20
// the variable of query all you can modify to fit into yours
module.exports.reportedStoreFromAndroidAppUsers = function(req, res, next)
{
    // testing
    var storeName = req.query.storeName;
    console.log(req.query);

    // check if the product ID is existing or not in product collection
    var productIdNumber = req.query.productId;
    Product.findById(productIdNumber, function(error, product)
    {
        if(error)
        {
            res.send("Something went wrong while searching the product!");
        }
        else if(!product)
        {
            res.send("The product is not existing!");
        }
        else
        {
            // set up the store info
            var reportedStoreData = {
                storeName: req.query.storeName,
                streetAddress: req.query.streetAddress,
                state: req.query.state,
                postCode: req.query.postCode,
                brandId: productIdNumber,
                brandName: product.Brand_Name,
                userId: req.query.userId
            };

            // create the store document
            ReportedStore.create(reportedStoreData, function (error, reportedStores)
            {
                if (error)
                {
                    var err = new Error('Store info is invalid!');
                    err.status = 400;
                    return next(err);
                }
                else
                {
                    res.send("Server has got your reported store!");
                }
            });
        }
    });
};

module.exports.loginRegisterAndroidAppFbUsers = function(req, res, next)
{
    // var queryStr = JSON.parse("[" + JSON.stringify(req.query) + "]");
    // console.log(queryStr.length);

    if(req.query.name == null)
    {
        // set up and receive the user info
        var facebookId = req.query.facebookId;
        console.log(facebookId);

        AppFbUser.findOne({facebookId: facebookId}, function (error, user)
        {
            if (error)
            {
                res.send("Error");
            }
            else if(!user)
            {
                res.send("No");
            }
            else
            {
                res.send("Yes");
            }
        });
    }
    else
    {
        // set up and receive the user info
        var facebookId = req.query.facebookId;
        console.log(facebookId);

        AppFbUser.findOne({facebookId: facebookId}, function (error, user)
        {
            if (error)
            {
                res.send("Error");
            }
            else if(!user)
            {
                // testing
                var name = req.query.name;
                console.log(name);

                // set up and receive the user info
                var appFbUserData = {
                    name: req.query.name,
                    facebookId: req.query.facebookId,
                    gender: req.query.gender,
                    birthday: req.query.birthday,
                    email: req.query.email
                };

                // create the user account
                AppFbUser.create(appFbUserData, function (error, appFbUser)
                {
                    if (error)
                    {
                        var err = new Error('User info is invalid!');
                        err.status = 400;
                        return next(err);
                    }
                    else
                    {
                        res.send("Create");
                    }
                });
            }
            else
            {
                res.send("Yes");
            }
        });
    }
};

module.exports.checkBrandVersion = function(req, res, next)
{
    Version.findOne({type: "brand"}, function(errorBrand, versionBrand)
    {
        if(errorBrand)
        {
            res.send("Error while finding brand!");
        }
        else
        {
            Version.findOne({type: "store"}, function(errorStore, versionStore)
            {
                if(errorStore)
                {
                    res.send("Error while finding store!");
                }
                else
                {
                    res.send(versionBrand.version + "," + versionStore.version);
                }
            });
        }
    });
};

module.exports.createStatistic = function(req, res, next)
{
    // var queryStr = JSON.parse("[" + JSON.stringify(req.query) + "]");
    // console.log(queryStr);
    // if(req.query.name == null)
    //     console.log("HI");
    // else
    //     console.log(("NONO"));

    // create a fake json string from android app
    // var test = [];
    // test.push({
    //     brandId: "5ccd8e9b3e36263b52a8d091",
    //     date: "06-05-2019",
    //     gender: "Male",
    //     age: "26",
    //     count: "11"
    // });
    // test.push({
    //     brandId: "5ccd8e9b3e36263b52a8d093",
    //     date: "07-05-2019",
    //     gender: "Male",
    //     age: "20",
    //     count: "5"
    // });
    // test.push({
    //     brandId: "5ccd8e9b3e36263b52a8d093",
    //     date: "01-05-2019",
    //     gender: "Female",
    //     age: "21",
    //     count: "19"
    // });
    // var testJson = JSON.stringify(test);
    // var testJson = JSON.stringify(req.query.statistic);
    // console.log(req.query.statistic);
    // console.log(testJson);

    // transfer json string back to json array
    // var statisticData = JSON.parse(testJson);
    var statisticData = JSON.parse(req.query.statistic);
    console.log(statisticData);

    // add the brand name depending on the brand id
    Product.find({}, function(errorFindAll, brands)
    {
        if(errorFindAll)
        {
            res.send("Something went wrong while searching the products!");
        }
        else if(!brands)
        {
            res.send("Do not have any product!");
        }
        else
        {
            // add brand name into each document based on their brand ID
            // (this step could implement in view as brand name may change later)
            // convert the date from string to date format
            // deal with the age into a range
            for(var i = 0; i < statisticData.length; i++)
            {
                // process the date
                var splitStr = statisticData[i].date.split("-");
                var formatDate = splitStr[2] + "-" + splitStr[1] + "-" + splitStr[0];
                statisticData[i].date = new Date(formatDate);

                // deal with the age
                var age = parseInt(statisticData[i].age);
                if(age < 18)
                    statisticData[i].age = "0-18";
                else if(age >= 18 && age <= 29)
                    statisticData[i].age = "18-29";
                else if(age >= 30 && age <= 39)
                    statisticData[i].age = "30-39";
                else if(age >= 40 && age <= 49)
                    statisticData[i].age = "40-49";
                else if(age >= 50 && age <= 59)
                    statisticData[i].age = "50-59";
                else if(age >= 60)
                    statisticData[i].age = "60+";
                else
                    statisticData[i].age = "Not Disclose";

                // add the brand name
                for(var j = 0; j < brands.length; j++)
                {
                    if(statisticData[i].brandId == brands[j]._id.toString())
                    {
                        statisticData[i].brandName = brands[j].Brand_Name;
                    }
                }
            }
            // console.log(statisticData);

            // check if the statistic data is existing already in the database
            Statistic.find({}, function(errorFindAllSta, statistics)
            {
                if(errorFindAllSta)
                {
                    res.send("Something went wrong while searching all statistic documents!");
                }
                else if(!statistics)
                {
                    // create statistic documents into mongo db if database is empty
                    Statistic.create(statisticData, function(errorCreate, result)
                    {
                        if(errorCreate)
                        {
                            res.send("Something went wrong while creating statistic documents!");
                        }
                        else
                        {
                            res.send("Yes");
                        }
                    });
                }
                else
                {
                    // check if database is not empty
                    var markStatisticData = [];
                    for(var i = 0; i < statisticData.length; i++)
                    {
                        var check = false;
                        for(var j = 0; j < statistics.length; j++)
                        {
                            if(statisticData[i].brandId == statistics[j].brandId && statisticData[i].date.getTime() === statistics[j].date.getTime()
                            && statisticData[i].gender == statistics[j].gender && statisticData[i].age == statistics[j].age)
                            {
                                var sumCount = (parseInt(statisticData[i].count) + parseInt(statistics[j].count)).toString();
                                check = true;
                                Statistic.findByIdAndUpdate(statistics[j]._id, {count: sumCount}, function(errorUpdate, updateRes)
                                {
                                    if(errorUpdate)
                                    {
                                        res.send("Something went wrong while updating statistic document!");
                                    }
                                });
                            }
                        }
                        if(check)
                        {
                            markStatisticData.push(1);
                        }
                        else
                        {
                            markStatisticData.push(0);
                        }
                    }
                    // console.log(markStatisticData);

                    // remove the elements as they exists in the database
                    var newStatisticData = [];
                    for(var i = 0; i < statisticData.length; i++)
                    {
                        if(markStatisticData[i] == 0)
                        {
                            newStatisticData.push(statisticData[i]);
                        }
                    }
                    // console.log(newStatisticData);

                    // add the remain statistic data to the database since they are new
                    Statistic.create(newStatisticData, function(errorCreate, result)
                    {
                        if(errorCreate)
                        {
                            res.send("Something went wrong while creating statistic documents!");
                        }
                        else
                        {
                            // res.redirect('/feature');
                            res.send("Yes");
                        }
                    });
                }
            });
        }
    });


};


/* * * * * * * * * * * * * * * * * *
 * Communication with the mongoDB  *
 * * * * * * * * * * * * * * * * * */

module.exports.GetAllBrand = async function(req, res, next)
{
    console.log("App is getting brands!");
    Product.find()
        .exec(function(errProduct,Product)
        {
            if(errProduct)
            {
                return next(errProduct);
            }
            else
            {
                res.json(Product);
            }
        })
}

module.exports.GetImage = async function(req,res,next)
{
    User.findById(req.session.userId)
        .exec(function(errorUser,user){
            if (errorUser) {
                return next(errorUser);
            } else {
                if (user === null) {
                    res.redirect('/');
                } else {
                    let imageid = req.query.imageid;
                    let imagepath = 'public/uploads/' + imageid+".jpg";
                    let image = fs.readFileSync(imagepath);
                    // let mime = fileType(image).mime;
                    res.end(image,'binary');
                }
            }
        })
}

module.exports.GetAllStore = async function(req, res, next)
{
    console.log("App is getting stores!");
    Store.find()
        .exec(function(errStore, Store)
        {
            if(errStore)
            {
                return next(errStore);
            }
            else
            {
                res.json(Store);
            }
        })
}

module.exports.GetAllStore = async function(req,res,next)
{
    Store.find({})
        .exec(function(errStore,store)
        {
            if(errStore)
            {
                return next(errStore);
            }
            else
            {
                res.json(store);
            }
        })
}


module.exports.GetAllBrandinStore = async function(req, res, next)
{
    BrandinStore.find()
        .exec(function(errBrandinStore,BrandinStore)
        {
            if(errBrandinStore)
            {
                return next(errBrandinStore);
            }
            else
            {
                Store.find()
                    .exec(function(errStore,Store)
                    {
                        if(errStore)
                        {
                            return next(errStore);
                        }
                        else
                        {
                            Product.find({})
                                .exec(function(errProduct,Product)
                                {
                                    var result = [];
                                    for (var i = 0 ; i < BrandinStore.length ; i ++)
                                    {
                                        for(var j = 0 ; j < Store.length ; j++)
                                        {
                                            if(new String(Store[j]._id).valueOf() == new String(BrandinStore[i].storeid).valueOf())
                                            {
                                                for(var k = 0 ; k < Store[j].Address.length ; k++)
                                                {
                                                    if(new String(Store[j].Address[k]._id).valueOf() == BrandinStore[i].addressid)
                                                    {
                                                        for(var l = 0 ; l < Product.length ; l ++)
                                                        {
                                                            if(new String(BrandinStore[i].brandid).valueOf() == new String(Product[l]._id).valueOf())
                                                            {
                                                                var newbrandinstore = {
                                                                    storeName: Store[j].storeName,
                                                                    StreetAddress: Store[j].Address[k].StreetAddress,
                                                                    State: Store[j].Address[k].State,
                                                                    Postcode: Store[j].Address[k].Postcode,
                                                                    Lat: Store[j].Address[k].Lat,
                                                                    Long: Store[j].Address[k].Long,
                                                                    Brandid: BrandinStore[i].brandid,
                                                                    Brandname: Product[l].Brand_Name
                                                                }
                                                                result.push(newbrandinstore);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    res.json(result);
                                })

                        }
                    })
            }
        })


}

module.exports.CompareStoreAddress = async function(req,res,next)
{
    Store.findById(req.query.storeid)
        .exec(function(errStore,store)
        {
            if(errStore)
            {
                return next(errStore);
            }
            else
            {
                if(store == null)
                {
                    res.redirect('/');
                }
                else
                {
                    var identicaladdress = false;
                    for(var i = 0 ; i < store.Address.length ; i++)
                    {
                        if(new String(store.Address[i].StreetAddress).toLowerCase().valueOf() == new String(req.query.address).toLowerCase().valueOf() && new String(store.Address[i].Postcode).valueOf() == new String(req.query.postcode))
                        {
                            identicaladdress = true;
                        }
                    }
                    if(identicaladdress)
                    {
                        res.json({
                            matched:false,
                            alert:"This address has already existed"
                        })
                    }
                    else
                    {
                        res.json({
                            matched:true
                        })
                    }
                }
            }
        })
}
