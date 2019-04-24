/**
 * Model for stores
 */

var mongoose = require('mongoose');

var StoreSchema = new mongoose.Schema(
    {
        storeName: String,
        Address: [{StreetAddress: String, Postcode: String, State: String, Lat: String, Long: String}],
        Product: [{ProductId: String}]
    },
    {
        versionKey: false
    });





var Store = mongoose.model('Store', StoreSchema, 'stores');

module.exports = Store;