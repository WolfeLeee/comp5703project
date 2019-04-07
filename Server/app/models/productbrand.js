var mongoose = require('./database');


var brandschema = new mongoose.Schema(
		{Name: {type: String, unique: true, required:true},
		Accreditation: [{
			Accreditation:String,
			Rating:String
		}],
		Image: [{
			type:String
		}],
		Category: String
		})


var Brand = mongoose.model('Brand',brandschema,'productbrand');

module.exports = Brand;