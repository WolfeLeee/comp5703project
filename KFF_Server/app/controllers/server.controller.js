var User = require("../models/user");
var Product = require("../models/product");

var mongoose = require('mongoose');
var csv = require('fast-csv');

var adminId = "";

// Basic pages with login and register
module.exports.goToLogin = function(req, res, next)
{
    User.count(function(error, count)
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
    var pwd = req.body.password;
    var pwdConf = req.body.passwordConfirm;
    if (pwd !== pwdConf)
    {
        var err = new Error('Password do NOT match!');
        err.status = 400;
        return next(err);
    }

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
                                    if(data.Brand_Name.localeCompare(products[i].Brand_Name)==0){
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
                                        Brand_Name: data.Brand_Name,
                                        Available: data.Available,
                                        Category: data.Category,
                                        Accreditation:[]
                                    }
                                    newdata.Accreditation.push({
                                        Accreditation: data.Accreditation,
                                        Rating: data.Rating
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
                                            if(products[i].Brand_Name.localeCompare(documents[j].Brand_Name) == 0){
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
                                                            Accreditation:products[i].Accreditation,
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
                    res.render('importPage.pug');
                }
            }
        });
};

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
                   res.render('productDetailPage/productDetailPage.pug',{
                       brandid: product._id,
                       brandname : product.Brand_Name,
                       brandcategory: product.Category,
                       accreditacount: product.Accreditation.length
                   });
               }
           }
        });
}

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

/** Update brand's summary information: Name, Category, Image, */
module.exports.ProductDetailPage_updateBrandSummary = async function(req,res,next)
{
    var Brand_Name = req.query.brand_name;
    var Brand_Category = req.query.brand_category;
    var Brand_Id = req.query.brand_id;
    Product.findById(Brand_Id)
        .exec(function(errorBrand, brand)
        {
            if(errorBrand)
            {
                return next(errorBrand);
            }
            else
            {
                Product.update({_id:Brand_Id},{$set:{Brand_Name:Brand_Name, Category:Brand_Category}})
                    .exec(function(errorUpdate){
                        if(errorUpdate){
                            return next(errorUpdate);
                        }
                        else {
                            console.log('successfully update brand')
                            res.redirect('/detailproductPage?productid=' + Brand_Id);
                        }
                    });
            }
        });

}


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
                    var perPage = 25 || req.query.perPage;
                    var page = (parseInt(req.query.page)) || 1;
                    var displayaccreditation = [];
                    for( var i = ((perPage * page) - perPage) ; i< product.Accreditation.length ; i++)
                    {
                        displayaccreditation.push(product.Accreditation[i]);
                    }

                    res.render('productDetailPage/productDetailPage_Accreditation.pug'
                        ,{
                        brandid: product._id,
                        brandname : product.Brand_Name,
                        brandcategory: product.Category,
                        accreditation: displayaccreditation
                    }
                    );
                }
            }
        });
}

// module.exports.databaseManagement = async function(req, res)
// {
//     var perPage = 25;
//     var page = (parseInt(req.query.page)) || 1;
//     let displaydata = Product.find({}).limit(perPage).skip((perPage * page) - perPage);
//     let count = await Product.count({});
//     let countentries = await Product.count({}).limit(perPage).skip((perPage * page) - perPage);
//     res.render('table.pug', {
//         displaydata: displaydata,
//         count: count,
//         current: page,
//         pages: Math.ceil(count/ perPage),
//         countentries: countentries
//     });
// }

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
                    var perPage = 25;
                    var page = (parseInt(req.query.page)) || 1;
                    Product.find({}).limit(perPage).skip((perPage * page) - perPage)
                        .exec(function(errFind, dataProducts)
                        {
                            if(errFind)
                                return next(errFind);
                            else
                            {
                                Product.count({})
                                    .exec(function(errCount, numOfProducts)
                                    {
                                        if(errCount)
                                            return next(errCount);
                                        else
                                        {
                                            Product.count({}).limit(perPage).skip((perPage * page) - perPage)
                                                .exec(function(errCountLimit, numOfLimitedProducts)
                                                {
                                                    if(errCountLimit)
                                                        return next(errCountLimit);
                                                    else
                                                    {
                                                        res.render('table.pug', {
                                                            displaydata: dataProducts,
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
            }
        });
}