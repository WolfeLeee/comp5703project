var express=require('express');
var controller=require('../controller/Brandcontroller.js');
var router=express.Router();

router.get('/BrandSearch',controller.searchbrand);
router.get('/AccreditationSearch',controller.searchaccreditation);


module.exports=router;