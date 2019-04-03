var express=require('express');
var controller=require('../controller/Reportcontroller.js');
var router=express.Router();

router.get('/',controller.storeswithspecificbrand);


module.exports=router;