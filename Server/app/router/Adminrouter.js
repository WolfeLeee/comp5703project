var express=require('express');
var controller=require('../controller/Admincontroller.js');
var router=express.Router();

router.get('/',controller.mainpage);


module.exports=router;