function openAddressForm() {
	$("#addAddress").hide();
	$("#address_form").show();
}

$("#cancel").click(function() {
	$("#addAddress").show();
	$("#address_form").hide();
})


//edit user
//update name
let nameflag = false;
$("#edit-btn-name").click(function() {

	if (nameflag == false) {
		event.preventDefault();
		$("#edit-name").prop('disabled', false);
		$("#name-save").show();
		$("#edit-btn-name").text("Cancel");
		nameflag = true;
	}
	else {
		event.preventDefault();
		$("#edit-name").prop('disabled', true);
		$("#name-save").hide();
		$("#edit-btn-name").text("Edit");
		nameflag = false;
	}
})


//update mobile number
let mobileflag = false;
$("#edit-btn-mobile").click(function() {

	if (mobileflag == false) {
		event.preventDefault();
		$("#edit-mobile").prop('disabled', false);
		$("#mobile-save").show();
		$("#edit-btn-mobile").text("Cancel");
		mobileflag = true;
	}
	else {
		event.preventDefault();
		$("#edit-mobile").prop('disabled', true);
		$("#mobile-save").hide();
		$("#edit-btn-mobile").text("Edit");
		mobileflag = false;
	}
})



// first request to create payment order
const paymentStart = (amount) => {
	// console.log("payment started");

	console.log(amount);
	if (amount == '' || amount == null) {
		// alert("amount is required !!");
		swal("Failed !!", "amount is required !!", "error");
		return;
	}

	//if amount is not null
	//we will use ajax to send request to server to create order - jquery
	$.ajax({
		url: '/user/create_order',
		data: JSON.stringify({ amount: amount, info: 'order_request' }),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',
		success: function(response) {
			//invoked when success
			console.log(response);
			if (response.status == 'created') {
				//open payment form
				let options = {
					key: 'rzp_test_dHwzHAFgHhBe2b',
					amount: response.amount,
					currency: 'INR',
					name: 'Eworld - E Shopping',
					description: 'Order Payment',
					image: 'https://creativeleaves.com/wp-content/uploads/2017/09/1.jpg',
					order_id: response.id,
					handler: function(response) {
						console.log(response.razorpay_payment_id);
						console.log(response.razorpay_order_id);
						console.log(response.razorpay_signature);
						console.log("Payment Successfull");
						// alert("Congrats !! Payment Successfull");

						updatePaymentOnServer(response.razorpay_payment_id, response.razorpay_order_id, 'paid');

					},
					prefill: {
						name: "",
						email: "",
						contact: ""
					},
					notes: {
						address: "Eworld Shopping"
					},
					theme: {
						color: "#3399cc"
					}
				};

				let rzp = new Razorpay(options);

				rzp.on('payment.failed', function(response) {
					console.log(response.error.code);
					console.log(response.error.description);
					console.log(response.error.source);
					console.log(response.error.step);
					console.log(response.error.reason);
					console.log(response.error.metadata.order_id);
					console.log(response.error.metadata.payment_id);
					// alert("Oops Payment Failed !!");
					swal("Failed !!", "Oops Payment Failed !!", "error");
				});

				rzp.open();

			}
		},
		error: function(error) {
			//invoked when error
			console.log(error);
			alert("something went wrong!!");
		}
	})

}


function updatePaymentOnServer(payment_id, order_id, status) {
	$.ajax({
		url: '/user/update_order',
		data: JSON.stringify({ payment_id: payment_id, order_id: order_id, status: status }),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',
		success: function(response) {
			swal("Congrats", "Payment Successfull", "success");
			window.location.href = "/user/my-orders";
		},
		error: function(error) {
			swal("Congrats", "Payment Successfull", "success");
			window.location.href = "/user/my-orders";
		}
	})
}


//search products
const search = () => {
	// console.log("searching");
	let query = $("#search-input").val();

	if (query == "") {
		$(".search-result").hide();
	}
	else {
		console.log(query);

		//sending request to server

		let url = `http://localhost:8080/search/${query}`;

		fetch(url).then((Response) => {
			return Response.json();
		}).then((data) => {
			console.log(data);

			let text = `<div class='list-group'>`

			data.forEach(product => {
				text += `<a href='/product/${product.pId}' class='list-group-item list-group-item-action'>${product.pName}</a>`
			});

			text += `</div>`

			$(".search-result").html(text);
			$(".search-result").show();

		});

		$(".search-result").show();
	}
}
