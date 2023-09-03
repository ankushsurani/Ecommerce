package com.eworld.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eworld.dto.FilterRequest;
import com.eworld.entities.CartItem;
import com.eworld.entities.Category;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.entities.WishlistItem;
import com.eworld.helper.FileImageUpload;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.CategoryPriorityService;
import com.eworld.services.CategoryService;
import com.eworld.services.ProductPriorityService;
import com.eworld.services.ProductService;
import com.eworld.services.UserService;
import com.eworld.services.WishlistItemService;

@Controller
public class HomeController {

	@Value("${product.image}")
	private String path;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CartService cartService;

	@Autowired
	private ProductPriorityService productPriorityService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryPriorityService categoryPriorityService;

	@Autowired
	private WishlistItemService wishlistItemService;

	@Value("${spring.application.name}")
	private String appName;

	private int pageSize = 12;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		List<CartItem> cartItems = new ArrayList<>();
		List<WishlistItem> wishlistItems = new ArrayList<>();
		int totalAmount = 0;
		int totalDiscountedAmount = 0;
		User user = null;
		if (principal != null) {
			user = this.userService.findByEmail(principal.getName());

			cartItems = this.cartService.findByUser(user);
			wishlistItems = this.wishlistItemService.getWishlistByUser(user);

			totalAmount = cartItems.stream()
					.mapToInt(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPrice()).sum();

			totalDiscountedAmount = cartItems.stream()
					.mapToInt(
							cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPriceAfterApplyingDiscount())
					.sum();
		}

		List<Category> categories = this.categoryService.getAllCategories();

		model.addAttribute("loggedIn", user != null).addAttribute("cartItems", cartItems)
				.addAttribute("wishlistItems", wishlistItems).addAttribute("totalAmount", totalAmount)
				.addAttribute("totalDiscountedAmount", totalDiscountedAmount);
		model.addAttribute("appName", this.appName);
		model.addAttribute("pageName", "home").addAttribute("categories", categories);
	}

	@GetMapping("/")
	public String homePage(Model model, HttpSession session) {

		model.addAttribute("title", "Home - Eworld");

		try {

			Pageable pageable = PageRequest.of(0, this.pageSize);

			Map<String, Product> highPriorityProducts = this.productPriorityService.getHighPriorityProducts();

			List<Category> highPriorityCategories = this.categoryPriorityService.getHighPrioCategories();

			Slice<Product> latestProducts = this.productService
					.getFilteredAndSortedProducts(new FilterRequest("Latest"), pageable);

			Slice<Product> topRatedProducts = this.productService
					.getFilteredAndSortedProducts(new FilterRequest("Rating"), pageable);

			Slice<Product> popularProducts = this.productService
					.getFilteredAndSortedProducts(new FilterRequest("Popularity"), pageable);

			model.addAttribute("highPriorityProducts", highPriorityProducts)
					.addAttribute("highPriorityCategories", highPriorityCategories)
					.addAttribute("latestProducts", latestProducts.getContent())
					.addAttribute("topRatedProducts", topRatedProducts.getContent())
					.addAttribute("popularProducts", popularProducts.getContent());

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "index-2";
	}

	// method to serve file
	@GetMapping(value = "/product_img/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable("imageName") String imageName, HttpServletResponse response)
			throws IOException {

		InputStream resource = FileImageUpload.getResource(path, imageName);

		response.setContentType(MediaType.IMAGE_JPEG_VALUE);

		StreamUtils.copy(resource, response.getOutputStream());

	}

	// search handler
	@GetMapping("/search/{query}")
	@ResponseBody
	public ResponseEntity<?> search(@PathVariable("query") String query) {

		List<Product> products = this.productService.getBypNameContaining(query);

		return ResponseEntity.ok(products);
	}

}