/**
 * Model for stores that are reported by app users
 */

var mongoose = require('mongoose');

var ReportedStoreSchema = new mongoose.Schema(
    {
        storeName: String,
        streetAddress: String,
        state: String,
        postCode: String,
        brandId: String,
        brandName: String
    },
    {
        versionKey: false
    });


var ReportedStore = mongoose.model('ReportedStore', ReportedStoreSchema, 'reportedStores');

module.exports = ReportedStore;