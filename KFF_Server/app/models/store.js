/**
 * Model for stores
 */

var mongoose = require('mongoose');

var StoreSchema = new mongoose.Schema(
    {
        brandId: String,
        category: String,
        storeName: String,
        Address: String
    },
    {
        versionKey: false
    });





var Store = mongoose.model('Store', StoreSchema, 'stores');

module.exports = Store;