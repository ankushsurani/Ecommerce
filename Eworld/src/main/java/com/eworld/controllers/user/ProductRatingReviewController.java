package com.eworld.controllers.user;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eworld.entities.CartItem;
import com.eworld.entities.Product;
import com.eworld.entities.ProductReview;
import com.eworld.entities.Rating;
import com.eworld.entities.User;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.OrderService;
import com.eworld.services.ProductReviewService;
import com.eworld.services.ProductService;
import com.eworld.services.RatingService;
import com.eworld.services.UserService;

@Controller
@RequestMapping("/user/add-review")
public class ProductRatingReviewController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private ProductReviewService productReviewService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CartService cartService;

	@Value("${spring.application.name}")
	private String appName;

	@ModelAttribute
	private void commonDetails(Model model, Principal principal) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());

			List<CartItem> cartItems = this.cartService.findByUser(user);

			int totalAmount = cartItems.stream()
					.mapToInt(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPrice()).sum();

			int totalDiscountedAmount = cartItems.stream()
					.mapToInt(
							cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPriceAfterApplyingDiscount())
					.sum();

			model.addAttribute("currentUser", user).addAttribute("cartItems", cartItems)
					.addAttribute("totalAmount", totalAmount)
					.addAttribute("totalDiscountedAmount", totalDiscountedAmount);
		}
		model.addAttribute("appName", this.appName).addAttribute("subPageName", "Product").addAttribute("pageName",
				"Ratings & Reviews");
	}

	@GetMapping("/{productId}")
	private String productRatingReviewPage(@PathVariable("productId") String productId, Principal principal,
			Model model) {

		Product product = this.productService.getProduct(productId);
		User user = this.userService.findByEmail(principal.getName());

		Rating rating = this.ratingService.getRatingByUserAndProduct(product, user);

		ProductReview productReview = new ProductReview();
		if (rating != null) {
			productReview = this.productReviewService.getProductReviewByRating(rating);
		}

		boolean hasUserOrderedProduct = this.orderService.hasUserOrderedProduct(user, product);

		model.addAttribute("productId", productId).addAttribute("rating", rating)
				.addAttribute("productReview", productReview)
				.addAttribute("hasUserOrderedProduct", hasUserOrderedProduct);

		return "user/write_review";
	}

	@PostMapping("/add-rating-process/{productId}")
	private String addRating(@RequestParam(name = "value", required = true) int rateValue,
			@PathVariable("productId") String productId, Principal principal, Model model, HttpSession session) {

		User user = this.userService.findByEmail(principal.getName());
		Product product = this.productService.getProduct(productId);

		try {

			Rating rating = this.ratingService.getRatingByUserAndProduct(product, user);

			if (rating == null) {
				rating = new Rating();
			}

			rating.setValue(rateValue);

			boolean hasUserOrderedProduct = this.ratingService.addRating(user, product, rating);

			if (!hasUserOrderedProduct) {
				return "redirect:/user/add-review/" + productId;
			}

			// save average Rating in product
			double avgRating = this.ratingService.getAvgRating(product);
			product.setAvgRating(avgRating);
			this.productService.saveProduct(product);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/add-review/" + productId;

	}

	@PostMapping("/add-review-process/{productId}")
	private String addReview(@Valid @ModelAttribute("productReview") ProductReview productReview, BindingResult result,
			@PathVariable("productId") String productId, Principal principal, Model model, HttpSession session) {

		User user = this.userService.findByEmail(principal.getName());
		Product product = this.productService.getProduct(productId);

		try {

			Rating rating = this.ratingService.getRatingByUserAndProduct(product, user);

			if (rating != null) {

				if (result.hasErrors()) {
					session.setAttribute("message", new Msg("Add valid Title and description", "alert-danger"));
				} else {
					productReview.setRating(rating);
					this.productReviewService.saveProductReview(productReview);

					session.setAttribute("message", new Msg("Your review has been saved", "alert-success"));
				}

			} else {
				session.setAttribute("message", new Msg("Give Rating First", "alert-danger"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something went wrong", "alert-danger"));
		}

		return "redirect:/user/add-review/" + productId;

	}

}
