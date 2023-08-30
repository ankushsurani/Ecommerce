package com.eworld.controllers.user;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eworld.entities.CartItem;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.ProductService;
import com.eworld.services.UserService;

@Controller
@RequestMapping("/user/cart")
public class CartController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private ProductService productService;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());

			List<CartItem> cartItems = this.cartService.findByUser(user);

			int totalAmount = cartItems.stream()
					.mapToInt(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPrice()).sum();

			int totalDiscountedAmount = cartItems.stream()
					.mapToInt(
							cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPriceAfterApplyingDiscount())
					.sum();

			model.addAttribute("cartItems", cartItems).addAttribute("totalAmount", totalAmount)
					.addAttribute("totalDiscountedAmount", totalDiscountedAmount);
		}
	}

	@GetMapping("")
	public String myCart(Model model) {
		model.addAttribute("title", "Mycart - Eworld");

		return "user/cart";
	}

	@GetMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable("productId") String productId, Principal principal, HttpSession session,
			Model model) {
		model.addAttribute("title", "Add CartItem - Eworld");

		try {

			Product product = this.productService.getProduct(productId);

			User user = this.userService.findByEmail(principal.getName());

			CartItem oldCart = this.cartService.findByProduct(product);
			if (oldCart != null && oldCart.getUser() == user) {
				session.setAttribute("message", new Msg("Product is alredy present in cart", "alert-warning"));
				return "redirect:/user/cart";
			}

			CartItem cartItem = new CartItem();

			cartItem.setCreatedDate(new Date());
			cartItem.setQuantity(1);
			cartItem.setProduct(product);
			cartItem.setUser(user);

			this.cartService.saveCartItem(cartItem);
			session.setAttribute("message",
					new Msg(cartItem.getProduct().getName() + " is added to Cart", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/cart";

	}

	// decrese quantity
	@GetMapping("/decrease-quantity/{cartItemId}")
	public String decreaseQuantity(@PathVariable("cartItemId") String cartItemId, Principal principal, Model model,
			HttpSession session) {
		model.addAttribute("title", "Decrease Item - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			CartItem cartItem = this.cartService.getCartItem(cartItemId);

			if (cartItem.getUser().getId().equals(user.getId())) {
				if (cartItem.getQuantity() > 1) {
					cartItem.setQuantity(cartItem.getQuantity() - 1);
					this.cartService.saveCartItem(cartItem);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/cart";
	}

	// increse quantity
	@GetMapping("/increase-quantity/{cartItemId}")
	public String increseQuantity(@PathVariable("cartItemId") String cartItemId, Principal principal, Model model,
			HttpSession session) {
		model.addAttribute("title", "Increase Item - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			CartItem cartItem = this.cartService.getCartItem(cartItemId);

			if (cartItem.getUser().getId().equals(user.getId())) {
				if (cartItem.getQuantity() < 5) {
					cartItem.setQuantity(cartItem.getQuantity() + 1);
					this.cartService.saveCartItem(cartItem);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/cart";
	}

	// remove cart item
	@GetMapping("/remove-cart-item/{cartId}")
	public String removeCart(@PathVariable("cartId") String cartId, Principal principal, HttpSession session,
			Model model) {
		model.addAttribute("title", "Remove Item - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			CartItem cartItem = this.cartService.getCartItem(cartId);

			if (cartItem.getUser().getId().equals(user.getId())) {
				this.cartService.removeCartItem(cartId);
				session.setAttribute("message",
						new Msg(cartItem.getProduct().getName() + " is removed from Cart", "alert-success"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/cart";
	}

}
