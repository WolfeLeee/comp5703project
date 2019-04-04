var express=require('express');
var path = require('path');
var Report = require('../models/userreport');
var Store = require('../models/storelocation');
var Brand = require('../models/productbrand');
var async = require("async");

/*RETURN ALL RELEVANT STORES WHICH HAVE A SPECIFIC BRAND*/

exports.storeswithspecificbrand = async function(req,res,next) {
	var querystring = req.query.query||"";
	var currentlat = req.query.lat;
	var currentlong = req.query.long;
	var locationradius = req.query.radius || 10;
	if(querystring.localeCompare("") == 0){
		try{
			let storeinfo = await Report.find({}).select({"storeid":1,"_id":0});
			var Storeidarray = [];
			for(var i = 0 ; i < storeinfo.length ; i++){
				Storeidarray.push(storeinfo[i].storeid);
			}
			let storewithbrands = await Store.find({"_id":{"$in":Storeidarray}});
			var storewhichisclosed = [];
			for(var i =0 ; i< storewithbrands.length ; i++){
				var closetome = distance(currentlat,currentlong,storewithbrands[i].Lat,storewithbrands[i].Long,"K");
				console.log(closetome);
				if(closetome < locationradius){
					storewhichisclosed.push(storewithbrands[i]);
				}
			}
			console.log(storewhichisclosed);
			res.send(storewhichisclosed);
		}
		catch(err){
			res.status(400).send("Error, could not retrieve the relevant store data");
		}	
	}
	else {
		try{
			let brandinfo = await Brand.find({"Name":{"$regex":querystring}}).select({"_id":1});
			var Brandidarray = [];
			for(var i = 0;i<brandinfo.length;i++){
				Brandidarray.push(brandinfo[i]._id);
			}
			let storeinfo = await Report.find({"brandid":{"$in":Brandidarray}}).select({"storeid":1,"_id":0});
			var Storeidarray = [];
			for(var i =0 ; i < storeinfo.length ; i++){
				Storeidarray.push(storeinfo[i].storeid);
			}
			let storewithbrands = await Store.find({"_id":{"$in":Storeidarray}});
			var storewhichisclosed = [];
			for(var i =0 ; i< storewithbrands.length ; i++){
				var closetome = distance(currentlat,currentlong,storewithbrands[i].Lat,storewithbrands[i].Long,"K");
				if(closetome < locationradius){
					storewhichisclosed.push(storewithbrands[i]);
				}
			}
			res.send(storewhichisclosed);
		}
		catch(errr){
			res.status(400).send("Error, could not retrieve the relevant store data");
		}
	}
}

/* FUNCTION TO CALCULATE THE DISTANCE BETWEEN PLACES USING LONG AND LAT */
function distance(lat1,lon1,lat2,lon2,unit){
	var radlat1 = Math.PI*lat1/180;
	var radlat2 = Math.PI*lat2/180;
	var theta = lon1 - lon2;
	var radtheta = Math.PI * theta/180;
	var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
	if(dist > 1){
		dist = 1;
	}
	dist = Math.acos(dist);
	dist = dist * 180/Math.PI;
	dist = dist * 60 * 1.1515;
	if(unit == "K"){
		dist = dist * 1.609344;
	}
	else if (unit == "N"){
		dist = dist * 0.8684;
	}
	return dist;
	
}


/* RETURN STORES WHICH HAVE RELEVANT STREET ADDRESS */
exports.allstorelocation = async function(req,res,next) {
	var querystring = req.query.query||"";
	if(querystring.localeCompare("") ==0){
		try{
			let alldata = await Store.find({});
			res.send(alldata);
		}
		catch(err){
		}	
	}
	else {
		try{
			let alldata = await Store.find({"StreetAddress":{"$regex":querystring,"$options":"i"}});
			res.send(alldata);
		}
		catch(err){
		}
	}
}

/* SAVE NEW STORE TO THE DATABASE */

exports.savestorelocation = async function(req,res,next){
	var Name = req.body.Name;
	var StreetAddress = req.body.StreetAddress;
	var StateTerritory = req.body.StateTerritory;
	var Postcode = req.body.Postcode;
	try{
		let alldata = await Store.find({});
		var nooverlap = false;
		for(var i = 0; i <alldata.length ;i++){
			if(alldata[i].StreetAddress.localeCompare(StreetAddress) == 0){
				nooverlap = true;
			}
		};
		if(nooverlap == false){
			var newstore = new Store ({
				Name: Name,
				StreetAddress: StreetAddress,
				StateTerritory: StateTerritory,
				Postcode: Postcode
			})
			newstore.save(function(err,results){
				if(err){
					console.log(err);
					res.status(200).send({Message:err});
				}
				else {
					console.log("Successfully save a new store");
					res.status(200).send({Message:"Successfully save a new store"});
				}
			})
		}
		else {
			res.status(200).send({Message:"The store has already existed"});
		}
	}
	catch (err){
	}
	
}