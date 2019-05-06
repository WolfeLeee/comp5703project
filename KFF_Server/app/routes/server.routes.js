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
router.post('/insertnewStore',controller.insertnewStore);
router.post('/insertnewBrand',controller.insertnewBrand);
router.post('/import', controller.importCSVFile);
router.get('/backFromSuccess', controller.backFromSuccess);
router.get('/importinsertPage', controller.goToImportPage);
router.get('/publish', controller.goToPublishPage);

// Report dbmanagement view
router.get('/report', controller.goToReportPage);
router.get('/report_Delete', controller.ReportPage_Delete);
router.post('/report_Insert', controller.ReportPage_Insert);

// Brand dbmanagement view
router.get('/dbmanagement', controller.databaseManagement);
router.get('/dbmanagement_Delete', controller.databaseManagement_Delete);

// Store dbmanagement view
router.get('/store_dbmanagement', controller.StoredatabaseManagement);
router.get('/store_dbmanagement_Delete', controller.StoredatabaseManagement_Delete);

// Publish Brand and Store Data
router.get('/publish/brand-data', controller.publishBrandData);
router.get('/publish/store-data', controller.publishStoreData);

// Get location data for a store using external API
router.get('/getLocation', controller.getLocation );

// Product Detail Page
router.get('/detailproductPage', controller.goToProductDetailPage);
router.get('/detailproductPage_Accreditation', controller.ProductDetailPage_Accreditation);
router.get('/detailproductPage_Accreditation__Delete', controller.ProductDetailPage_Accreditation__Delete);
router.get('/detailproductPage_Accreditation__Update', controller.ProductDetailPage_Accreditation__Update);
router.get('/detailproductPage_Accreditation__Insert', controller.ProductDetailPage_Accreditation__Insert);
router.post('/detailproductPage_updateBrandSummary', controller.ProductDetailPage_updateBrandSummary);

// Store Detail Page
router.get('/detailstorePage', controller.goToStoreDetailPage);
router.post('/detailstorePage_updateBrandSummary', controller.StoreDetailPage_updateStoreSummary);
router.get('/detailstorePage_Brand',controller.StoreDetailPage_Brand);
router.get('/detailstorePage_Brand__Delete',controller.StoreDetailPage_Brand__Delete);
router.get('/detailstorePage_Brand__Insert', controller.StoreDetailPage_Brand__Insert);
router.get('/detailstorePage_Address',controller.StoreDetailPage_Address);
router.get('/detailstorePage_Address__Delete',controller.StoreDetailPage_Address__Delete);
router.post('/detailstorePage_Address__Insert',controller.StoreDetailPage_Address__Insert);
router.get('/detailstorePage_Address__FindBrandNotInthis',controller.StoreDetailPage_Address__FindBrandNotInthis);

// Retrieve messages from Android app
router.get('/android-app-register', controller.registerAndroidAppUsers);
router.get('/android-app-login', controller.loginAndroidAppUsers);
router.get('/android-app-report-store', controller.reportedStoreFromAndroidAppUsers);
router.get('/android-app-login-register-fb', controller.loginRegisterAndroidAppFbUsers);
router.get('/android-app-check-version-brand', controller.checkBrandVersion);
router.get('/android-app-check-version-store', controller.checkStoreVersion);
router.get('/android-app-statistic', controller.createStatistic);

// Get data from the database
router.get('/GetAllBrand',controller.GetAllBrand);
router.get('/GetImage',controller.GetImage);
router.get('/GetAllStore',controller.GetAllStore);
router.get('/CompareStoreAddress',controller.CompareStoreAddress);

module.exports = router;