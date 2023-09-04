package com.eworld.controllers.user;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eworld.entities.CartItem;
import com.eworld.entities.Category;
import com.eworld.entities.Product;
import com.eworld.entities.ProductCompareItem;
import com.eworld.entities.User;
import com.eworld.entities.WishlistItem;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.CategoryService;
import com.eworld.services.ProductCompareService;
import com.eworld.services.ProductService;
import com.eworld.services.UserService;
import com.eworld.services.WishlistItemService;

@Controller
@RequestMapping("/user/product-compare")
public class ProductCompareController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private WishlistItemService wishlistItemService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductCompareService productCompareService;

	@Value("${spring.application.name}")
	private String appName;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		User user = null;
		if (principal != null) {
			user = this.userService.findByEmail(principal.getName());

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
		model.addAttribute("pageName", "Compare Product");
		model.addAttribute("subPageName", "Products");
	}

	@GetMapping("")
	private String compareProduct(Principal principal, Model model) {

		User user = this.userService.findByEmail(principal.getName());
		List<ProductCompareItem> productCompareItemsList = this.productCompareService
				.getAllProductFromComparisonByUser(user);

		List<List<Object>> productCompareItems = productCompareItemsList.stream().map(productCompare -> {
			LocalDate estimatedDeliveryDate = LocalDate.now()
					.plusDays(productCompare.getProduct().getEstimatedDeliveryDays());

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
			String formattedEstimatedDeliveryDate = estimatedDeliveryDate.format(dateFormatter);

			return List.of(productCompare, formattedEstimatedDeliveryDate);
		}).collect(Collectors.toList());

		model.addAttribute("productCompareItems", productCompareItems);

		return "user/compare_products";
	}

	@GetMapping("/add-compare-product/{productId}")
	private String addCompareProduct(@PathVariable("productId") String productId, Principal principal,
			HttpSession session) {
		User user = this.userService.findByEmail(principal.getName());
		Product product = this.productService.getProduct(productId);

		List<ProductCompareItem> productCompareItems = this.productCompareService
				.getAllProductFromComparisonByUser(user);

		if (productCompareItems.size() < 4) {

			if (productCompareItems.size() > 1 && !productCompareItems.get(0).getProduct().getCategory().getId()
					.equals(product.getCategory().getId())) {

				session.setAttribute("message", new Msg(product.getName()
						+ " is from different category you only can comparison between same category of products",
						"alert-warning"));

				return "redirect:/product/page=0?categoryId="
						+ productCompareItems.get(0).getProduct().getCategory().getId();

			} else {
				if (!this.productCompareService.existsByUserAndProduct(user, product)) {
					this.productCompareService.addProductIntoComparison(new ProductCompareItem(product, user));
					session.setAttribute("message",
							new Msg(product.getName() + " product Added for comparison", "alert-success"));
				} else {
					session.setAttribute("message",
							new Msg(product.getName() + " is Already in comparison", "alert-warning"));
				}
			}

		} else {
			session.setAttribute("message", new Msg("You Already have 4 products in comparison", "alert-danger"));
		}

		return "redirect:/user/product-compare";
	}

	@GetMapping("/remove-product-from-comparison/{productCompareItemId}")
	private String removeProductFromComparison(@PathVariable("productCompareItemId") String productCompareItemId,
			Principal principal, HttpSession session) {

		try {

			User user = this.userService.findByEmail(principal.getName());
			ProductCompareItem productCompareItem = this.productCompareService
					.getProductFromComparisonById(productCompareItemId);

			if (user.getId().equals(productCompareItem.getUser().getId())) {
				this.productCompareService.removeProductFromComparison(productCompareItemId);
				session.setAttribute("message", new Msg(
						productCompareItem.getProduct().getName() + " Removed from comparison", "alert-success"));
			} else {
				session.setAttribute("message", new Msg("Something Went Wrong", "alert-danger"));
			}

		} catch (Exception e) {
			session.setAttribute("message", new Msg("Something Went Wrong", "alert-danger"));
		}

		return "redirect:/user/product-compare";

	}

}
