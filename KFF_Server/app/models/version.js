/**
 * Model for version that shows the current version of database
 */

var mongoose = require('mongoose');

var VersionSchema = new mongoose.Schema(
    {
        version: String,
        type: String
    },
    {
        versionKey: false
    });


var Version = mongoose.model('Version', VersionSchema, 'versions');

module.exports = Version;