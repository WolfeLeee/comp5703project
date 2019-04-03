var mongoose = require('./database');


var storeschema = new mongoose.Schema(
		{Name: {type: String, required:true},
		StreetAddress: {type: String, unique: true, required:true},
		Lat:String,
		Long:String,
		StateTerritory: String,
		Postcode:Number
		})


var Store = mongoose.model('Store',storeschema,'storelocation');

module.exports = Store;