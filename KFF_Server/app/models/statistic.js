/**
 * Model for statistic that includes a brand and its search history
 */

var mongoose = require('mongoose');

var StatisticSchema = new mongoose.Schema(
    {
        brandName: String,
        brandId: String,
        date: Date,
        gender: String,
        age: String,
        count: Number
    },
    {
        versionKey: false
    });


var Statistic = mongoose.model('Statistic', StatisticSchema, 'statistics');

module.exports = Statistic;