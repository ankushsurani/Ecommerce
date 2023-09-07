package com.eworld.controllers.user;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eworld.dto.AccountOrderDto;
import com.eworld.entities.Address;
import com.eworld.entities.CartItem;
import com.eworld.entities.Category;
import com.eworld.entities.Order;
import com.eworld.entities.Payment;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.entities.WishlistItem;
import com.eworld.enumstype.DeliveryStatus;
import com.eworld.helper.Msg;
import com.eworld.services.AddressService;
import com.eworld.services.CartService;
import com.eworld.services.CategoryService;
import com.eworld.services.OrderService;
import com.eworld.services.PaymentService;
import com.eworld.services.ProductService;
import com.eworld.services.UserService;
import com.eworld.services.WishlistItemService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
@RequestMapping("/user/order")
public class OrderController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private WishlistItemService wishlistItemService;

	@Autowired
	private CategoryService categoryService;

	@Value("${spring.application.name}")
	private String appName;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());
			model.addAttribute("user", user);

			List<Address> addresses = this.addressService.getAddressByUser(user);
			model.addAttribute("addresses", addresses);

			List<CartItem> cartItems = this.cartService.findByUser(user);
			List<WishlistItem> wishlistItems = this.wishlistItemService.getWishlistByUser(user);

			int totalAmount = cartItems.stream()
					.mapToInt(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPrice()).sum();

			int totalDiscountedAmount = cartItems.stream()
					.mapToInt(
							cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPriceAfterApplyingDiscount())
					.sum();

			model.addAttribute("loggedIn", user != null).addAttribute("cartItems", cartItems)
					.addAttribute("wishlistItems", wishlistItems).addAttribute("totalAmount", totalAmount)
					.addAttribute("totalDiscountedAmount", totalDiscountedAmount);

		}

		List<Category> categories = this.categoryService.getAllCategories();
		model.addAttribute("categories", categories);

		model.addAttribute("appName", this.appName);
		model.addAttribute("pageName", "Cart Details");
		model.addAttribute("subPageName", "Cart");

	}

	@GetMapping("/checkout-order")
	public String placeOrder(@RequestParam(name = "productId", required = false) String productId, Principal principal,
			Model model) {
		model.addAttribute("title", "Place Order - Eworld");
		model.addAttribute("address", new Address());

		User user = this.userService.findByEmail(principal.getName());

		List<CartItem> cartItems = new ArrayList<>();
		int totalDiscountedAmount = 0;

		if (productId != null) {
			Product product = this.productService.getProduct(productId);

			boolean presentInCart = this.cartService.existsByUserAndProduct(user, product);

			CartItem cartItem;
			if (presentInCart) {
				cartItem = this.cartService.getByUserAndProduct(user, product);
			} else {
				cartItem = new CartItem(new Date(), product, user, 1);
				this.cartService.saveCartItem(cartItem);
			}
			cartItems.add(cartItem);
			totalDiscountedAmount = cartItem.getProduct().getPriceAfterApplyingDiscount();
		} else {
			cartItems = this.cartService.findByUser(user);

			totalDiscountedAmount = cartItems.stream()
					.mapToInt(
							cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPriceAfterApplyingDiscount())
					.sum();
		}
		model.addAttribute("checkoutCartItems", cartItems).addAttribute("totalDiscountedAmount", totalDiscountedAmount);

		if (cartItems.size() == 0) {
			return "redirect:/user/cart";
		}
		return "user/checkout";
	}

	@PostMapping("/add-address")
	public String addAddress(@Valid @ModelAttribute("address") Address address, BindingResult result, Model model,
			Principal principal, HttpSession session) {

		model.addAttribute("title", "Add Address - Eworld");

		try {

			if (result.hasErrors()) {
				model.addAttribute("address", address);
				return "user/checkout";
			}

			User user = this.userService.findByEmail(principal.getName());

			address.setUser(user);

			this.addressService.changeAddressStatus(user, "MAIN", true);
			address.setAddressType("MAIN");

			this.addressService.saveAddress(address);

			session.setAttribute("message", new Msg("Address Added Successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/order/checkout-order";

	}

	@PostMapping("/send-order-cod")
	public String submitOrder(@RequestParam("orderAddress") String addressId,
			@RequestParam("cartItemId") List<String> cartItemIds, @RequestParam("paymentOption") String paymentOption,
			Principal principal, Model model, HttpSession session) {
		model.addAttribute("title", "Submit Order - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			Address address = this.addressService.getAddressByUserAndId(user, addressId);

			List<CartItem> cartItems = this.cartService.getCartItemsByIds(cartItemIds);

			for (CartItem cartItem : cartItems) {
				Order order = new Order();

				order.setCreatedDate(LocalDateTime.now());
				order.setAddress(address);
				order.setProduct(cartItem.getProduct());
				order.setQuantity(cartItem.getQuantity());
				order.setUser(user);
				order.setPaymentType(paymentOption);
				order.setOrderPrice(cartItem.getProduct().getPriceAfterApplyingDiscount() * cartItem.getQuantity());

				LocalDateTime estimatedDeliveryDate = LocalDateTime.now()
						.plusDays(cartItem.getProduct().getEstimatedDeliveryDays());
				order.setDeliveryDate(estimatedDeliveryDate);

				if (paymentOption.equals("Cash On Delivery")) {
					order.setDeliveryStatus(DeliveryStatus.AWAITINGPICKUP);
					this.cartService.removeCartItem(cartItem.getId());
				}

				else {
					order.setDeliveryStatus(DeliveryStatus.AWAITINGPAYMENT);
				}

				this.orderService.saveOrder(order);

			}

			if (paymentOption.equals("Cash On Delivery")) {
				session.setAttribute("message", new Msg("Your Order Is Sent !! Continue Shopping...", "alert-success"));
				return "redirect:/user/account/orders";
			} else {
				session.setAttribute("message",
						new Msg("Your Order Is in waiting select payment mode and Confirm Order", "alert-success"));
				return "redirect:/user/order/checkout-order?payMode=online";
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("Something Went Wrong!!", "alert-danger");
			return "redirect:/user/cart";
		}

	}

	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) {

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

			User user = this.userService.findByEmail(principal.getName());
			List<Order> orders = this.orderService.findOrdersByUserAndStatuses(user.getId(),
					List.of(DeliveryStatus.AWAITINGPAYMENT));

			// save order in database
			for (Order productOrder : orders) {
				Payment payment = new Payment();

				int rupeeAmount = order.get("amount");

				payment.setAmount(rupeeAmount / 100);
				payment.setRzporderId(order.get("id"));
				payment.setPaymentId(null);
				payment.setStatus("created");
				payment.setCreatedDate(LocalDateTime.now());
				payment.setOrder(productOrder);
				payment.setReceipt(order.get("receipt"));

				this.paymentService.savePayment(payment);
			}

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

				Order order = this.orderService.findById(payment.getOrder().getId());
				order.setDeliveryStatus(DeliveryStatus.AWAITINGPICKUP);
				this.orderService.saveOrder(order);
			}

			User user = this.userService.findByEmail(principal.getName());
			List<CartItem> cartItems = this.cartService.findByUser(user);
			for (CartItem cartItem : cartItems) {
				this.cartService.removeCartItem(cartItem.getId());
			}

			session.setAttribute("message", new Msg("Your Order Is Send !! Continue Shopping...", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("Something Went Wrong!!", "alert-danger");
		}

		return "redirect:/user/account/orders";
	}

}
