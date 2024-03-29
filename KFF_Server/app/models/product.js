/**
 * Model for products
 */

var mongoose = require('mongoose');
// var objectId = mongoose.Types.ObjectId();

var ProductSchema = new mongoose.Schema(
    {
        Brand_Name: String,
        Accreditation:[{
            Accreditation:String,
            Rating:String
        }],
        Available: String,
        Category: String,
        Image: String
    },
    {
        versionKey: false
    });



var Product = mongoose.model('Product', ProductSchema, 'products');

module.exports = Product;