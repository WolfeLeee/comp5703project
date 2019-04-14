/**
 * Model for products
 */

var mongoose = require('mongoose');

var ProductSchema = new mongoose.Schema(
    {
        Brand_Name: String,
        // Accreditation: String,
        // Rating: String,
        Accreditation:[{
            Accreditation:String,
            Rating:String
        }],
        Available: String,
        Category: String,
        Image:String
    },
    {
        versionKey: false
    });





var Product = mongoose.model('Product', ProductSchema, 'products');

module.exports = Product;