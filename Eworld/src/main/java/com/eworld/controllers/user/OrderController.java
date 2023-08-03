package com.eworld.controllers.user;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eworld.entities.Address;
import com.eworld.entities.Cart;
import com.eworld.entities.Order;
import com.eworld.entities.Payment;
import com.eworld.entities.User;
import com.eworld.helper.Msg;
import com.eworld.services.AddressService;
import com.eworld.services.CartService;
import com.eworld.services.OrderService;
import com.eworld.services.PaymentService;
import com.eworld.services.UserService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
@RequestMapping("/user")
public class OrderController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PaymentService paymentService;

	private String paymentMode;

	private List<Order> orders;

	private boolean billVisible;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());
			model.addAttribute("currentUser", user);

			List<Address> addresses = this.addressService.getAddressByUser(user);
			model.addAttribute("addresses", addresses);

			List<Cart> carts = this.cartService.findByUser(user);

			if (carts.size() == 0) {
				model.addAttribute("cart", 0);
			} else {
				int totalPrice = 0;
				int totalDiscoutedPrice = 0;
				for (Cart c : carts) {
					totalPrice += c.getProduct().getpPrice() * c.getQuantity();
					totalDiscoutedPrice += c.getProduct().getPriceAfterApplyingDiscount() * c.getQuantity();
				}

				model.addAttribute("totalPrice", totalPrice);
				model.addAttribute("totalDiscountPrice", totalDiscoutedPrice);

				model.addAttribute("cart", carts);
			}
		}
	}

	@PostMapping("/place-order")
	public String placeOrder(@RequestParam("orderAmount") int orderAmount, Model model) {
		model.addAttribute("title", "Place Order - Eworld");

		model.addAttribute("orderAmount", orderAmount);
		model.addAttribute("address", new Address());

		return "user/place_order";
	}

	@PostMapping("/submit-order")
	public String submitOrder(@RequestParam("orderAddress") int AddressId,
			@RequestParam("paymentOption") String paymentOption, @RequestParam("orderAmount") int orderAmount,
			Principal principal, Model model, HttpSession session) {
		model.addAttribute("title", "Submit Order - Eworld");

		try {

			Address address = this.addressService.getAddressById(AddressId);

			User user = this.userService.getUserById(address.getUser().getUserId());

			List<Cart> carts = this.cartService.findByUser(user);

			this.paymentMode = paymentOption;

			this.orders = new ArrayList<>();

			for (Cart cart : carts) {
				Order order = new Order();

				order.setCreatedDate(new Date());
				order.setAddress(address);
				order.setProduct(cart.getProduct());
				order.setQuantity(cart.getQuantity());
				order.setUser(user);
				order.setPaymentType(paymentOption);
				order.setTotalPayment(orderAmount);

				if (paymentOption.equals("Cash On Delivery")) {
					order.setStatus("Awaiting Pickup");
					this.cartService.removeCart(cart.getCartId());
				}

				else {
					order.setStatus("Awaiting Payment");
				}

				this.orderService.saveOrder(order);

				this.orders.add(order);

			}

			if (paymentOption.equals("Cash On Delivery")) {
				session.setAttribute("message", new Msg("Your Order Is Send !! Continue Shopping...", "alert-success"));
				return "redirect:/user/my-orders";
			} else {
				this.billVisible = true;
				return "redirect:/user/order-bill";
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("Something Went Wrong!!", "alert-danger");
			return "redirect:/user/mycart";
		}

	}

	@GetMapping("/order-bill")
	public String orderBill(Model model) {
		if (this.billVisible == false) {
			return "redirect:/";
		}

		model.addAttribute("title", "Order Invoice - Eworld");

		model.addAttribute("paymentMode", this.paymentMode);

		return "user/order_bill";

	}

	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) {

		int amount = Integer.parseInt(data.get("amount").toString());

		try {

			RazorpayClient client = new RazorpayClient("rzp_test_dHwzHAFgHhBe2b", "8Hm4KU1VT6XgGXeGjvhZlNRU");

			JSONObject object = new JSONObject();
			object.put("amount", amount * 100);
			object.put("currency", "INR");
			object.put("receipt", "txn_235425");

			// creating new order
			com.razorpay.Order order = client.orders.create(object);
			System.out.println(order);

			// save order in database
			for (Order productOrder : this.orders) {
				Payment payment = new Payment();

				int rupeeAmount = order.get("amount");

				payment.setAmount(rupeeAmount / 100);
				payment.setRzporderId(order.get("id"));
				payment.setPaymentId(null);
				payment.setStatus("created");
				payment.setOrder(productOrder);
				payment.setReceipt(order.get("receipt"));

				this.paymentService.savePayment(payment);
			}

			this.orders = null;

			return order.toString();

		} catch (RazorpayException e) {
			e.printStackTrace();
			return "error";
		}

	}

	// after success payment we update our database
	@PostMapping("/update_order")
	public String updateOrder(@RequestBody Map<String, Object> data, Principal principal, HttpSession session) {

		try {

			List<Payment> payments = this.paymentService.findByRzporderId(data.get("order_id").toString());

			for (Payment payment : payments) {
				payment.setPaymentId(data.get("payment_id").toString());
				payment.setStatus(data.get("status").toString());

				this.paymentService.savePayment(payment);

				Order order = this.orderService.findById(payment.getOrder().getOrder_id());
				order.setStatus("Awaiting Pickup");
				this.orderService.saveOrder(order);
			}

			this.billVisible = false;

			User user = this.userService.findByEmail(principal.getName());
			List<Cart> carts = this.cartService.findByUser(user);
			for (Cart cart : carts) {
				this.cartService.removeCart(cart.getCartId());
			}

			session.setAttribute("message", new Msg("Your Order Is Send !! Continue Shopping...", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("Something Went Wrong!!", "alert-danger");
		}

		return "redirect:/";
	}

	// show orders to user
	@GetMapping("/my-orders")
	public String myOrders(Principal principal, Model model, HttpSession session) {
		model.addAttribute("title", "My Orders - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			List<Order> orders = this.orderService.getOrderByUser(user);
			model.addAttribute("allorder", orders);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("Something Went Wrong!!", "alert-danger");
		}

		return "user/my_orders";
	}

}
