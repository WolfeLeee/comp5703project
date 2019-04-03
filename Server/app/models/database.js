var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/KinderFoodFinder',function(){
	console.log('mongodb connected')
});

module.exports = mongoose;


