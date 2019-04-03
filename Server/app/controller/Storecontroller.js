var express=require('express');
var path = require('path');
var Store = require('../models/storelocation');
var async = require("async");


/* RETURN STORES WHICH HAVE RELEVANT STREET ADDRESS */
exports.allstorelocation = async function(req,res) {
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
		catch(errr){
		}
	}
}

/* SAVE NEW STORE TO THE DATABASE */

exports.savestorelocation = async function(req,res){
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