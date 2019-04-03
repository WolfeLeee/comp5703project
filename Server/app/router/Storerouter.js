var express=require('express');
var controller=require('../controller/Storecontroller.js');
var router=express.Router();

router.get('/',controller.storeswithspecificbrand);
router.post('/savestore',controller.savestorelocation);


module.exports=router;