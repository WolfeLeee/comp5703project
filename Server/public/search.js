$(document).ready(function(){
	
	$('.inputform_brand__BrandSubmit').click(function(){
		var brandname = document.getElementById('inputform_brand__BrandName').value;
		var category = document.getElementById('inputform_brand__BrandCategory').value;
		var organization = document.getElementsByClassName('inputform_brand__BrandAccreditation');
		var rating = document.getElementsByClassName('inputform_brand__BrandRating');
		var accreditation = [];
		for ( var i = 0; i<organization.length; i++){
			var singleaccr = {
				Accreditation:organization[i].value,
				Rating: rating[i].value
			}

			accreditation.push(JSON.stringify(singleaccr));
		}
		var params = {
			Brandname:brandname,
			Category: category,
			Accreditation:accreditation
		}
		var jqxhr = $.get("/Searching/Savenewbrand",params)
			jqxhr.done(function(result){
				console.log("Success")
			});
			jqxhr.fail(function(jqxhr){
				console.log(jqxhr.status);
			});
	})
	
	function post(path, params) {
		var jqxhr = $.post(path,params);
		jqxhr.donefunction();
		jqxhr.fail(function(jqxhr){
			console.log(jqxhr.status);
		});
	}
})