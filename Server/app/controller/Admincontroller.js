var express=require('express');
var path = require('path');
var Report = require('../models/userreport');
var Store = require('../models/storelocation');
var Brand = require('../models/productbrand');
var async = require("async");


exports.mainpage = async function(req,res){
	res.render('mainpage.ejs');
}

exports.databasemanagement = async function(req,res){
	var perPage = 25;
	var page = (parseInt(req.query.page)) || 1;
	let displaydata = await Brand.find({}).limit(perPage).skip((perPage*page) - perPage);
	let count = await Brand.count({});
	let countentries = await Brand.count({}).limit(perPage).skip((perPage*page)-perPage);
	res.render('table.jade',{
		displaydata:displaydata,
		count:count,
		current: page,
		pages: Math.ceil(count/ perPage),
		countentries:countentries
	})


}

exports.searchpage = async function(req,res){
	res.render('searchpage.ejs');
}