var express = require('express');
var controller = require('../controllers/server.controller');
var router = express.Router();

// Basic
router.get('/', controller.goToLogin);
router.get('/landing', controller.showLandingPage);
router.post('/', controller.registerLogin);
router.get('/feature', controller.goToFeature);
router.get('/logout', controller.logout);

// Main page and some features
router.post('/insertnewBrand',controller.insertnewBrand);
router.post('/import', controller.importCSVFile);
router.get('/backFromSuccess', controller.backFromSuccess);
router.get('/importinsertPage', controller.goToImportPage);
router.get('/dbmanagement', controller.databaseManagement);

// Product Detail Page
router.get('/detailproductPage', controller.goToProductDetailPage);
router.get('/detailproductPage_Accreditation', controller.ProductDetailPage_Accreditation);
router.get('/detailproductPage_Accreditation__Delete', controller.ProductDetailPage_Accreditation__Delete);
router.get('/detailproductPage_Accreditation__Update', controller.ProductDetailPage_Accreditation__Update);
router.get('/detailproductPage_Accreditation__Insert', controller.ProductDetailPage_Accreditation__Insert);
router.post('/detailproductPage_updateBrandSummary', controller.ProductDetailPage_updateBrandSummary);

// Retrieve messages from Android app
router.get('/android-app-register', controller.registerAndroidAppUsers);
router.get('/android-app-login', controller.loginAndroidAppUsers);

// Get data from the database
router.get('/GetAllBrand',controller.GetAllBrand);

module.exports = router;