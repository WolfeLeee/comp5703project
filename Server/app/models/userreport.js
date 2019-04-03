var mongoose = require('./database');


var Reportschema = new mongoose.Schema(
		{userid: String,
		brandid: String,
		storeid: String	
		})


var Report = mongoose.model('Report',Reportschema,'userreport');

module.exports = Report;