$(document).ready(function(){
	
	$('.inputform_brand__BrandSubmit').click(function(){
		var brandname = document.getElementById('inputform_brand__BrandName').value;
		var category = document.getElementById('inputform_brand__BrandCategory').value;
		var organization = document.getElementsByClassName('inputform_brand__BrandAccreditation');
		var rating = document.getElementsByClassName('inputform_brand__BrandRating');
		var image = document.getElementById('inputform_brand__BrandImage').files[0];
		var accreditation = [];
		for ( var i = 0; i<organization.length; i++){
			var singleaccr = {
				Accreditation:organization[i].value,
				Rating: rating[i].value
			}
			accreditation.push(JSON.stringify(singleaccr));
		}
		var formdata = new FormData();
		formdata.append('Brandname',brandname);
		formdata.append('Category',category);
		formdata.append('Accreditation',accreditation);
		formdata.append('Image',image,image.name);
		var request = new XMLHttpRequest();
		request.open("POST","/Searching/Savenewbrand");
		request.send(formdata);
	})
	
	function post(path, params) {
		var jqxhr = $.post(path,params);
		jqxhr.donefunction();
		jqxhr.fail(function(jqxhr){
			console.log(jqxhr.status);
		});
	}
})