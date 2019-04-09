/**
 * Model for products
 */

var mongoose = require('mongoose');

var ProductSchema = new mongoose.Schema(
    {
        Accreditation: String,
        Brand_Name: String,
        Rating: String,
        Available: String,
        Category: String
    },
    {
        versionKey: false
    });





var Product = mongoose.model('Product', ProductSchema, 'products');

module.exports = Product;