$(document).ready(function() {
    var user = $('#name').text();
    if(user !== "" || user !== undefined){
        startDapp();
	$('form').submit(function(event) {
		var formId = this.id,
			form = this;
		event.preventDefault();
		setTimeout( function () { 
			form.submit();
		}, 1000);
	});
    }
})

var startDapp = async function() {
	getMyItems();
	getAllRegisteredItems();
	getAllOrderedItems();
	getBalance();
}


var getBalance = function() {
    var xhr = new XMLHttpRequest();
    var user = $('#name').text();
    xhr.onreadystatechange = function() {
    if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201) {
                let parsed = JSON.parse(xhr.response);
                if(parsed.balance === undefined)    parsed.balance = "0"
                $("#mybalance").text("Balances : " + parsed.balance + " Token");
            } else {
                console.error(xhr.responseText);
            }
        }
    };
    xhr.open('POST', 'http://localhost:3000/getBalance');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({'user':user}));
}


var registerItem = async function() {
    var xhr = new XMLHttpRequest();
    var user = $('#name').text();
    val = $("#brand").val();
    xhr.onreadystatechange = function() {
    if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201) {
                location.reload(true);
            } else {
                console.error(xhr.responseText);
            }
        }
    };
    xhr.open('POST', 'http://localhost:3000/registerItem');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({'user':user, 'brand':val }));
}

var sellMyItem = async function() {
    var xhr = new XMLHttpRequest();
    var user = $('#name').text();
    var target = $("#myItems-category option:selected").val();
    xhr.onreadystatechange = function() {
    if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201) {
                location.reload(true);
            } else {
                console.error(xhr.responseText);
            }
        }
    };
    xhr.open('POST', 'http://localhost:3000/sellMyItem');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({'user':user, 'key':target, 'price':$('#price').val()}));
}

var buyUserItem = async function() {
    var xhr = new XMLHttpRequest();
    var user = $('#name').text();
    var target = $("#sale-category option:selected").val();
    xhr.onreadystatechange = function() {
    if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201) {
                location.reload(true);
            } else {
                console.error(xhr.responseText);
            }
        }
    };
    xhr.open('POST', 'http://localhost:3000/buyUserItem');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({'user':user, 'key':target}));
}

var getMyItems = async function() {
    var xhr = new XMLHttpRequest();
    var user = $('#name').text();
    xhr.onreadystatechange = function() {
    if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201) {
                let parsed = JSON.parse(xhr.response);
                let result = eval(parsed.result);
                for(var i in result) {
                    var text = "<tr><td>" + result[i]["key"]
                    +"</td><td>" + result[i]["record"]["owner"]
                    +"</td><td>" + result[i]["record"]["name"]
                    +"</td></tr>";
                    $('#myItems').append(text);
                }
                for (var i in result) {
                    if(!result[i]["record"]["marketState"])
                        $("#myItems-category").append(new Option(result[i]["key"], result[i]["key"]));
                }
            } else {
                console.error(xhr.responseText);
            }
        }
    };
    xhr.open('POST', 'http://localhost:3000/getMyItems');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({'user':user}));
}

var getAllRegisteredItems = async function() {
    var xhr = new XMLHttpRequest();
    var user = $('#name').text();
    xhr.onreadystatechange = function() {
    if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201) {
                let parsed = JSON.parse(xhr.response);
                let result = eval(parsed.result);
                for(var i in result) {
                    var text = "<tr><td>" + result[i]["key"]
                    +"</td><td>" + result[i]["record"]["owner"]
                    +"</td><td>" + result[i]["record"]["name"]
                    +"</td></tr>";
                    $('#registeredItems').append(text);
                }
            } else {
                console.error(xhr.responseText);
            }
        }
    };
    xhr.open('POST', 'http://localhost:3000/getAllRegisteredItems');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({'user':user}));
}

var earnToken = async function() {
    var xhr = new XMLHttpRequest();
    var user = $('#name').text();
    val = $("#brand").val();
    xhr.onreadystatechange = function() {
    if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201) {
                location.reload(true);
            } else {
                console.error(xhr.responseText);
            }
        }
    };
    xhr.open('POST', 'http://localhost:3000/earnToken');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({'user':user}));
}

var getAllOrderedItems = async function() {
    var xhr = new XMLHttpRequest();
    var user = $('#name').text();
    xhr.onreadystatechange = function() {
    if (xhr.readyState === xhr.DONE) {
        if (xhr.status === 200 || xhr.status === 201) {
            let parsed = JSON.parse(xhr.response);
            divided = parsed.result.split('&');
            let result1 = eval(divided[0]);
            let result2 = eval(divided[1]);
            for(var i in result1) {
                var text = "<tr><td>" + result1[i]["key"]
                +"</td><td>" + result1[i]["record"]["owner"]
                +"</td><td>" + result1[i]["record"]["name"]
                +"</td><td>" + result1[i]["record"]["price"]
                +"</td><td>" + "on Sale"
                +"</td></tr>";
                $('#ItemOnSale').append(text);
            }
            for(var i in result2) {
                var temp = eval(result2[i])
                var text = "<tr><td>" + temp[0]
                +"</td><td>" + temp[2]
                +"</td><td>" + temp[1]
                +"</td><td>" + temp[3]
                +"</td><td>" + "Done"
                +"</td></tr>";
                $('#ItemOnSale').append(text);
            }
            for (var i in result1) {
                    if(result1[i]["record"]["owner"] !== user)
                        $("#sale-category").append(new Option(result1[i]["key"], result1[i]["key"]));
            }
        } else {
            console.error(xhr.responseText);
        }
        }
    };
    xhr.open('POST', 'http://localhost:3000/getAllOrderedItems');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({'user':user}));
}


