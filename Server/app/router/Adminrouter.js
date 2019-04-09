var express=require('express');
var controller=require('../controller/Admincontroller.js');
var router=express.Router();

router.get('/',controller.mainpage);
router.get('/searchpage',controller.searchpage);
router.get('/dbmanagement',controller.databasemanagement);


module.exports=router;