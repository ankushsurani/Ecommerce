package com.eworld.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eworld.dto.FilterRequest;
import com.eworld.entities.CartItem;
import com.eworld.entities.Category;
import com.eworld.entities.Product;
import com.eworld.entities.ProductReview;
import com.eworld.entities.User;
import com.eworld.entities.WishlistItem;
import com.eworld.services.CartService;
import com.eworld.services.CategoryService;
import com.eworld.services.ProductReviewService;
import com.eworld.services.ProductService;
import com.eworld.services.RatingService;
import com.eworld.services.UserService;
import com.eworld.services.WishlistItemService;

@Controller
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CartService cartService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private ProductReviewService productReviewService;
	
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

			model.addAttribute("currentUser", user);
		}
		model.addAttribute("loggedIn", user != null).addAttribute("cartItems", cartItems)
		.addAttribute("wishlistItems", wishlistItems)
				.addAttribute("totalAmount", totalAmount).addAttribute("totalDiscountedAmount", totalDiscountedAmount);
		model.addAttribute("appName", this.appName);

		List<Category> categories = this.categoryService.getAllCategories();
		model.addAttribute("categories", categories);
	}

	@GetMapping("/page={pageNum}")
	public String filterProducts(@PathVariable("pageNum") int pageNum,
			@RequestParam(name = "sortType", required = false) String sortType,
			@RequestParam(name = "categoryId", required = false) String categoryId,
			@RequestParam(name = "minPrice", required = false) Integer minPrice,
			@RequestParam(name = "maxPrice", required = false) Integer maxPrice,
			@RequestParam(name = "brandName", required = false) String brandName, Model model) {

		try {

			FilterRequest filterRequest = new FilterRequest(sortType, categoryId, minPrice, maxPrice, brandName);

			Pageable pageable = PageRequest.of(pageNum, this.pageSize);

			Slice<Product> products = productService.getFilteredAndSortedProducts(filterRequest, pageable);
			boolean hasNextPage = products.hasNext();

			model.addAttribute("products", products.getContent()).addAttribute("hasNextPage", hasNextPage);

			String categoryTitle = "All Category";

			if (categoryId != null) {
				categoryTitle = this.categoryService.getTitleById(categoryId);
			}

			model.addAttribute("pageName", categoryTitle);

			List<String> allBrandName = this.productService.getAllBrandName(categoryId);
			model.addAttribute("allBrandName", allBrandName).addAttribute("subPageName", "Products");

		} catch (Exception e) {
			System.out.println("Something Went Wrong");
		}

		return "products";
	}

	@GetMapping("/{productId}")
	public String showProductDetails(@PathVariable("productId") String productId, Model model) {
		Product product = this.productService.getProduct(productId);

		Pageable pageable = PageRequest.of(0, this.pageSize);
		List<Product> similarProducts = this.productService
				.getSimilarProductsByCatId(product.getCategory().getId(), pageable).getContent().stream()
				.filter(produc -> !produc.getId().equals(productId)).collect(Collectors.toList());

		List<String> ratingIds = this.ratingService.getRatingsIdsByProduct(product);
		List<ProductReview> reviews = this.productReviewService.getProductReviewsByIds(ratingIds);

		LocalDate estimatedDeliveryDate = LocalDate.now().plusDays(product.getEstimatedDeliveryDays());

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
		String formattedEstimatedDeliveryDate = estimatedDeliveryDate.format(dateFormatter);

		model.addAttribute("product", product).addAttribute("deliveryDate", formattedEstimatedDeliveryDate)
				.addAttribute("reviews", reviews).addAttribute("pageName", product.getName())
				.addAttribute("subPageName", "Product").addAttribute("similarProducts", similarProducts);

		return "product_details";
	}

}