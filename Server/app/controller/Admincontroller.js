var express=require('express');
var path = require('path');
var Report = require('../models/userreport');
var Store = require('../models/storelocation');
var Brand = require('../models/productbrand');
var async = require("async");


exports.mainpage = async function(req,res){
	res.render('mainpage.ejs');
}