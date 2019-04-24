// Models
var User = require("../models/user");
var Product = require("../models/product");
var AppUser = require("../models/appUser");
var AppFbUser = require("../models/appFbUser");
var Store = require("../models/store");


// External libraries
var mongoose = require('mongoose');
var csv = require('fast-csv');
var fs = require('fs');
var request = require('request');

// Global variables
var adminId = "";


/* * * * * * * * * *
 * Wolfe's coding  *
 * * * * * * * * * */

// Basic pages with login and register
module.exports.goToLogin = function(req, res, next)
{
    User.countDocuments(function(error, count)
    {
        if (error)
        {
            //var err = new Error('Username has been used!');
            error.status = 400;
            return next(error);
        }
        else
        {
            if(count == 0)
            {
                // create admin acc at the beginning
                var userData = {
                    username: "admin",
                    password: "admin"
                };

                User.create(userData, function (error, user)
                {
                    if (error)
                    {
                        //var err = new Error('Username has been used!');
                        error.status = 400;
                        return next(error);
                    }
                    else
                    {
                        res.redirect('/landing');
                    }
                });
            }
            else
            {
                res.redirect('/landing');
            }
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
    // confirm that user typed same password twice if register
    // var pwd = req.body.password;
    // var pwdConf = req.body.passwordConfirm;
    // if (pwd !== pwdConf)
    // {
    //     var err = new Error('Password do NOT match!');
    //     err.status = 400;
    //     return next(err);
    // }

    // // register
    // if (req.body.firstName && req.body.lastName && req.body.username && req.body.password && req.body.passwordConfirm && req.body.email)
    // {
    //     var userData = {
    //         firstName: req.body.firstName,
    //         lastName: req.body.lastName,
    //         username: req.body.username,
    //         password: req.body.password,
    //         passwordConfirm: req.body.passwordConfirm,
    //         email: req.body.email,
    //     };
    //
    //     User.create(userData, function (error, user)
    //     {
    //         if (error)
    //         {
    //             //var err = new Error('Username has been used!');
    //             error.status = 400;
    //             return next(error);
    //         }
    //         else
    //         {
    //             req.session.userId = user._id;
    //             return res.redirect('/feature');
    //         }
    //     });
    //
    // }
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
            if (error || !user)
            {
                var err = new Error('Something wrong when reset admin account!');
                err.status = 401;
                return next(err);
            }
            else
            {
                console.log("Admin account has been reset!");
                res.redirect('/landing');
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

// Feature page
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
                    res.render('main.pug');
                }
            }
        });

};

/* * * * * * * * * * * * * * * * * *
 * Insert/ Import data             *
 * * * * * * * * * * * * * * * * * */

module.exports.insertnewStore = async function(req,res,next)
{
    console.log(req.body);
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
            res.redirect('/');
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
                    res.redirect('/importPage');
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

// Revision.findArticleHighestRev(function(errorArticleHighestRev, resultArticleHighestRev)
// {
//     if(errorArticleHighestRev || !resultArticleHighestRev)
//     {
//         var err = new Error('Something wrong or result not found!');
//         err.status = 401;
//         return next(errorArticleHighestRev);
//     }
//     else
//     {
//         Revision.findArticleLargeRegUserEdit(function(errorArticleLargeRegUserEdit, resultArticleLargeRegUserEdit)
//         {
//             if(errorArticleLargeRegUserEdit || !resultArticleLargeRegUserEdit)
//             {
//                 var err = new Error('Something wrong or result not found!');
//                 err.status = 401;
//                 return next(errorArticleLargeRegUserEdit);
//             }
//             else
//             {
//                 Revision.findArticleLongHistory(function(errorArticleLongHistory, resultArticleLongHistory)
//                 {
//                     if(errorArticleLongHistory || !resultArticleLongHistory)
//                     {
//                         var err = new Error('Something wrong or result not found!');
//                         err.status = 401;
//                         return next(errorArticleLongHistory);
//                     }
//                     else
//                     {
//                         Revision.findArticleShortHistory(function(errorArticleShortHistory, resultArticleShortHistory)
//                         {
//                             if (errorArticleShortHistory || !resultArticleShortHistory)
//                             {
//                                 var err = new Error('Something wrong or result not found!');
//                                 err.status = 401;
//                                 return next(errorArticleShortHistory);
//                             }
//                             else
//                             {
//                                 // res.render('feature.pug', {username: user.username, email: user.email,
//                                 //     firstName: user.firstName, lastName: user.lastName, resultTitleHighestRev:
//                                 //     resultArticleHighestRev, resultArticleEditedByLargestReUser:
//                                 //     resultArticleLargeRegUserEdit, resultArticleLongHistory: resultArticleLongHistory,
//                                 //     resultArticleShortHistory: resultArticleShortHistory});
//                                 Revision.findAllTitlesRev(function(errorAllTitlesRev, resultAllTitlesRev)
//                                 {
//                                     if (errorAllTitlesRev || !resultAllTitlesRev)
//                                     {
//                                         var err = new Error('Something wrong or result not found!');
//                                         err.status = 401;
//                                         return next(errorAllTitlesRev);
//                                     }
//                                     else
//                                     {
//                                         res.render('feature.pug', {username: user.username, email: user.email,
//                                             firstName: user.firstName, lastName: user.lastName, resultTitleHighestRev:
//                                             resultArticleHighestRev, resultArticleEditedByLargestReUser:
//                                             resultArticleLargeRegUserEdit, resultArticleLongHistory: resultArticleLongHistory,
//                                             resultArticleShortHistory: resultArticleShortHistory, resultAllTitlesRev: resultAllTitlesRev});
//                                     }
//                                 });
//                             }
//                         });
//                     }
//                 });
//             }
//         });
//     }
// });

/* * * * * * * * * *
 * Jordan's coding *
 * * * * * * * * * */

/** Direct user to the product detail page.
 * Hence, the product page that will be displayed depends on the productid that is passed to this module
 */
module.exports.goToProductDetailPage = function (req,res,next)
{
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
                    if(product.Image == null || product.Image.localeCompare("")==0){
                        res.render('productDetailPage/productDetailPage.pug',{
                            brandid: product._id,
                            brandname : product.Brand_Name,
                            brandcategory: product.Category,
                            brandimage: "null",
                            accreditacount: product.Accreditation.length
                        });
                    }
                    else {
                        res.render('productDetailPage/productDetailPage.pug',{
                            brandid: product._id,
                            brandname : product.Brand_Name,
                            brandcategory: product.Category,
                            brandimage: product.Image,
                            accreditacount: product.Accreditation.length
                        });
                    }

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


module.exports.databaseManagement = function(req, res, next)
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
                    var perPage = (parseInt(req.query.product)) || 25;
                    var page = (parseInt(req.query.page)) || 1;
                    Product.find({}).limit(perPage).skip((perPage * page) - perPage)
                        .exec(function(errFind, dataProductsPerPage)
                        {
                            if(errFind)
                                return next(errFind);
                            else
                            {
                                Product.countDocuments({})
                                    .exec(function(errCount, numOfProducts)
                                    {
                                        if(errCount)
                                            return next(errCount);
                                        else
                                        {
                                            Product.countDocuments({}).limit(perPage).skip((perPage * page) - perPage)
                                                .exec(function(errCountLimit, numOfLimitedProducts)
                                                {
                                                    if(errCountLimit)
                                                        return next(errCountLimit);
                                                    else
                                                    {
                                                        Product.find({})
                                                            .exec(function(errFindAll, dataAllProducts)
                                                            {
                                                               if(errFindAll)
                                                                   return next(errFindAll);
                                                               else
                                                               {
                                                                   res.render('table.pug', {
                                                                       displaydata: dataProductsPerPage,
                                                                       displayAllData: dataAllProducts,
                                                                       numPerPage: perPage,
                                                                       count: numOfProducts,
                                                                       current: page,
                                                                       pages: Math.ceil(numOfProducts/ perPage),
                                                                       countentries: numOfLimitedProducts
                                                                   });
                                                               }
                                                            });
                                                    }
                                                });
                                        }
                                    });
                            }
                        });
                }
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
    // set up and receive the user info
    var name = req.query.name;
    var gender = req.query.gender;
    var email = req.query.email;
    var password = req.query.password;
    var birthday = req.query.birthday;
    console.log(name);

    var appUserData = {
        name: req.query.name,
        gender: req.query.gender,
        email: req.query.email,
        password: req.query.password,
        birthday: req.query.birthday
    };

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
            res.send("Server has got your data!");
        }
    });

    // req.addListener("data", function(postDataChunk)
    // {
    //     // data += postDataChunk;
    //     data = req.query.name;
    // });
    //
    // req.addListener("end", function()
    // {
    //     console.log('Welcome to register server!');
    //     console.log("data: " + data);
    // });
};

module.exports.loginAndroidAppUsers = function(req, res, next)
{
    // set up and receive the user info
    var email = req.query.email;
    var password = req.query.password;
    console.log(email);

    AppUser.authenticate(email, password, function (error, user)
    {
        if (error || !user)
        {
            res.send("No");
        }
        else
        {
            res.send("Yes");
        }
    });
};


/* * * * * * * * * * * * * * * * * *
 * Communication with the mongoDB  *
 * * * * * * * * * * * * * * * * * */

module.exports.GetAllBrand = async function(req, res, next)
{
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
