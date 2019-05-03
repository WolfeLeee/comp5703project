/**
 * Model for statistic that includes a brand and its search history
 */

var mongoose = require('mongoose');

var StatisticSchema = new mongoose.Schema(
    {
        brandName: String,
        brandId: String,
        week: String,
        gender: String,
        age: String,
        count: String
    },
    {
        versionKey: false
    });


var Statistic = mongoose.model('Statistic', StatisticSchema, 'statistics');

module.exports = Statistic;