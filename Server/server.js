var express=require('express');
var app=express();
var bodyparser=require('body-parser');
var path=require('path');
var Brandrouter=require('./app/router/Brandrouter.js');
var Storerouter=require('./app/router/Storerouter.js');
var Adminrouter=require('./app/router/Adminrouter.js');
app.use(bodyparser.json());
app.set('views',path.join(__dirname,'app/views'));
app.set('view engine','jade');
app.use(express.static(path.join(__dirname,'public')));
app.use(bodyparser.urlencoded());
app.use('/Searching',Brandrouter);
app.use('/Store',Storerouter);
app.use('/',Adminrouter);

app.listen(3000,function(){
    console.log('survey app listening on port 3000');
});