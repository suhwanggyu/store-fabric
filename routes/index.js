var express = require('express');
var enroll = require('../fabric_js/enrollAdmin.js')
var register = require('../fabric_js/registerUser.js')
var query = require('../fabric_js/query.js')
var invoke = require('../fabric_js/invoke.js')
var router = express.Router();

let user;

/* GET home page. */
router.get('/', function(req, res, next) {
 	res.render('index', { name: req.cookies.user });
});

router.get('/enrollAdmin', async function(req, res, next) {
	await enroll.enrollAdmin();
	res.redirect('/');
})

router.post('/registerUser', async function(req, res, next) {
	user = req.body.user;
	await register.registerUser(user);
	res.cookie('user', user);
	res.redirect('/');
})

router.post('/registerItem', async function(req, res, next) {
	user = req.body.user;
	await invoke.invoke(user, "registerItem", [req.body.brand, user]);
	res.cookie('user', user);
	res.redirect('/');
})

router.post('/earnToken', async function(req, res, next) {
	user = req.body.user;
	await invoke.invoke(user, "transfer", ["admin", user,"100"]);
	res.cookie('user', user);
	res.redirect('/');
        res.redirect(req.originalUrl);
})


router.post('/getMyItems', async function(req, res, next) {
	user = req.body.user;
	var result = await query.query("getMyItems", user);
	res.cookie('user', user);
	res.status(200).json({"result":result});
})

router.post('/getBalance', async function(req, res, next) {
	user = req.body.user;
	var result = await query.query("getBalance", user);
	res.cookie('user', user);
	res.status(200).json({"balance":result});
})

router.post('/sellMyItem', async function(req, res, next) {
	user = req.body.user;
	await invoke.invoke(user, "sellMyItem", [req.body.key, req.body.price]);
	res.cookie('user', user);
	res.redirect('/');
})

router.post('/buyUserItem', async function(req, res, next) {
	user = req.body.user;
	await invoke.invoke(user, "buyUserItem", [req.body.key, user]);
	res.cookie('user', user);
	res.redirect('/');
})

router.post('/getAllRegisteredItems', async function(req, res, next) {
	user = req.body.user;
	var result = await query.query("getAllRegisteredItems","admin");
	res.cookie('user', user);
	res.status(200).json({"result":result});
})

router.post('/getAllOrderedItems', async function(req, res, next) {
	user = req.body.user;
	var result = await query.query("getAllOrderedItems", "admin");
	res.cookie('user', user);
	res.status(200).json({"result":result});
})



module.exports = router;
