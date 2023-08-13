package com.eworld.controllers.user;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eworld.entities.Cart;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.ProductService;
import com.eworld.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
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
			model.addAttribute("currentUser", user);

			List<Cart> carts = this.cartService.findByUser(user);

			if (carts.size() == 0) {
				model.addAttribute("cart", 0);
			} else {
				int totalPrice = 0;
				int totalDiscoutedPrice = 0;
				for (Cart c : carts) {
					totalPrice += c.getProduct().getPrice() * c.getQuantity();
					totalDiscoutedPrice += c.getProduct().getPriceAfterApplyingDiscount() * c.getQuantity();
				}

				model.addAttribute("totalPrice", totalPrice);
				model.addAttribute("totalDiscountPrice", totalDiscoutedPrice);

				model.addAttribute("cart", carts);
			}
		}
	}

	@GetMapping("/mycart")
	public String myCart(Model model) {
		model.addAttribute("title", "Mycart - Eworld");

		return "user/cart";
	}

	@GetMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable("productId") String productId, Principal principal, HttpSession session,
			Model model) {
		model.addAttribute("title", "Add CartItem - Eworld");

		try {

			if (principal == null) {
				session.setAttribute("message",
						new Msg("Please login if you want to add product in cart", "alert-warning"));
				return "redirect:/login";
			}

			Product product = this.productService.getProduct(productId);

			User user = this.userService.findByEmail(principal.getName());

			Cart oldCart = this.cartService.findByProduct(product);
			if (oldCart != null && oldCart.getUser() == user) {
				session.setAttribute("message", new Msg("Product is alredy present in cart", "alert-warning"));
				return "redirect:/user/mycart";
			}

			Cart cart = new Cart();

			cart.setCreatedDate(new Date());
			cart.setQuantity(1);
			cart.setProduct(product);
			cart.setUser(user);

			this.cartService.saveCart(cart);
			session.setAttribute("message",
					new Msg(cart.getProduct().getName() + " is added to Cart", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/mycart";

	}

	// decrese quantity
	@GetMapping("/decrease-quantity/{cartId}")
	public String decreaseQuantity(@PathVariable("cartId") String cartId, Principal principal, Model model,
			HttpSession session) {
		model.addAttribute("title", "Decrease Item - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			Cart cart = this.cartService.getCart(cartId);

			if (cart.getUser().getId().equals(user.getId())) {
				if (cart.getQuantity() > 1) {
					cart.setQuantity(cart.getQuantity() - 1);
					this.cartService.saveCart(cart);

					// update bill
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/mycart";
	}

	// increse quantity
	@GetMapping("/increase-quantity/{cartId}")
	public String increseQuantity(@PathVariable("cartId") String cartId, Principal principal, Model model,
			HttpSession session) {
		model.addAttribute("title", "Increase Item - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			Cart cart = this.cartService.getCart(cartId);

			if (cart.getUser().getId().equals(user.getId())) {
				if (cart.getQuantity() < 5) {
					cart.setQuantity(cart.getQuantity() + 1);
					this.cartService.saveCart(cart);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/mycart";
	}

	// remove cart item
	@GetMapping("/remove-cart-item/{cartId}")
	public String removeCart(@PathVariable("cartId") String cartId, Principal principal, HttpSession session,
			Model model) {
		model.addAttribute("title", "Remove Item - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			Cart cart = this.cartService.getCart(cartId);

			if (cart.getUser().getId().equals(user.getId())) {
				this.cartService.removeCart(cartId);
				session.setAttribute("message",
						new Msg(cart.getProduct().getName() + " is removed from Cart", "alert-success"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/mycart";
	}

}
