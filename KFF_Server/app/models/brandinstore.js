/**
 * Model for brandinstore
 */

var mongoose = require('mongoose');

var BrandinStoreSchema = new mongoose.Schema(
    {
        storeid: String,
        brandid: String,
        addressid: String
    },
    {
        versionKey: false
    });

var BrandinStore = mongoose.model('BrandinStore', BrandinStoreSchema, 'BrandinStore');

module.exports = BrandinStore;