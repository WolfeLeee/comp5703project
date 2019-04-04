var express=require('express');
var path = require('path');
var Brand = require('../models/productbrand');
var async = require("async");


/*SEARCH BRAND'S RATING BY BRAND NAME*/

exports.searchbrand = async function(req,res,next) {
	var searchfilter = req.query.filter||"";
	var querystring = req.query.query||"";
	if(querystring.localeCompare("") ==0){
		if(searchfilter.localeCompare("")==0){
			try{
				let alldata = await Brand.find({});
				res.send(alldata);
			}
			catch(err){
				res.status(400).send("Could not find the relevant brands");
			}
		}
		else{
			try{
				let alldata = await Brand.find({"Category":searchfilter});
				res.send(alldata);
			}
			catch(err){
				res.status(400).send("Could not find the relevant brands");
			}
		}
	}
	else {
		if(searchfilter.localeCompare("") ==0){
			try{
				let alldata = await Brand.find({"Name":{"$regex":querystring,"$options":"i"}});
				res.send(alldata);
			}
			catch(err){
				res.status(400).send("Could not find the relevant brands");
			}
		}
		else {
			try{
				let alldata = await Brand.find({"$and":[{"Name":{"$regex":querystring,"$options":"i"},"Category":searchfilter}]});
				res.send(alldata);
			}
			catch(err){
				res.status(400).send("Could not find the relevant brands");
			}
		}
	}
}

/*SEARCH BRAND'S RATING BY ACCREDITATION*/

exports.searchaccreditation = async function(req,res,next) {
	var searchfilter = req.query.filter||"";
	var querystring = req.query.query||"";
	if(querystring.localeCompare("") ==0){
		if(searchfilter.localeCompare("")==0){
			try{
				let alldata = await Brand.find({});
				res.send(alldata);
			}
			catch(err){
				res.status(400).send("Could not find the relevant brands");
			}
		}
		else{
			try{
				let alldata = await Brand.find({"Category":searchfilter});
				res.send(alldata);
			}
			catch(err){
				res.status(400).send("Could not find the relevant brands");
			}
		}
	}
	else {
		if(searchfilter.localeCompare("") == 0){
			try{
				let alldata = await Brand.find({});
				var relevantbrands = [];
				alldata.forEach(function(brand){
					var isrelevant = false;
					for( var i =0;i<brand.Accreditation.length ; i++){
						if( brand.Accreditation[i].Accreditation.toLowerCase().indexOf(querystring.toLowerCase()) !== -1){
							isrelevant = true;
						}
					}
					if(isrelevant){
						relevantbrands.push(brand);
					}
				})
				res.send(relevantbrands);
			}
			catch(err){
				res.status(400).send("Could not find the relevant brands");
			}
		}
		else {
			try{
				let alldata = await Brand.find({"Category":searchfilter});
				var relevantbrands = [];
				alldata.forEach(function(brand){
					var isrelevant = false;
					for( var i =0;i<brand.Accreditation.length ; i++){
						if( brand.Accreditation[i].Accreditation.toLowerCase().indexOf(querystring.toLowerCase()) !== -1){
							isrelevant = true;
						}
					}
					if(isrelevant){
						relevantbrands.push(brand);
					}
				})
				res.send(relevantbrands);
			}
			catch(err){
				res.status(400).send("Could not find the relevant brands");
			}
		}
	}
}