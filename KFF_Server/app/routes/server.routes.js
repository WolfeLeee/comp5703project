var express = require('express');
var controller = require('../controllers/server.controller');
var router = express.Router();

// Basic
router.get('/', controller.goToLogin);
router.get('/landing', controller.showLandingPage);
router.post('/', controller.registerLogin);
router.get('/feature', controller.goToFeature);
router.get('/logout', controller.logout);

// Main page
router.post('/import', controller.importCSVFile);
router.get('/backFromSuccess', controller.backFromSuccess);

router.get('/importPage', controller.goToImportPage);
router.get('/dbmanagement', controller.databaseManagement);
router.get('/detailproductPage',controller.goToProductDetailPage);
router.get('/detailproductPage_Accreditation',controller.ProductDetailPage_Accreditation);
router.get('/detailproductPage_Accreditation__Delete',controller.ProductDetailPage_Accreditation__Delete);
router.get('/detailproductPage_updateBrandSummary',controller.ProductDetailPage_updateBrandSummary);

module.exports = router;