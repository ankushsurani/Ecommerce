package com.eworld.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eworld.entities.Cart;
import com.eworld.entities.Category;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.helper.FileImageUpload;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.CategoryPriorityService;
import com.eworld.services.CategoryService;
import com.eworld.services.OrderService;
import com.eworld.services.ProductPriorityService;
import com.eworld.services.ProductService;
import com.eworld.services.UserService;

@Controller
public class HomeController {

	@Value("${product.image}")
	private String path;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private ProductPriorityService productPriorityService;

	@Autowired
	private CategoryPriorityService categoryPriorityService;

	@Autowired
	private OrderService orderService;

	@Value("${spring.application.name}")
	private String appName;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());
			model.addAttribute("currentUser", user);

			List<Cart> carts = this.cartService.findByUser(user);
			model.addAttribute("cart", carts);
		}
		model.addAttribute("appName", this.appName);
		model.addAttribute("pageName", "home");
	}

	/*
	 * @GetMapping("/") public String home(@RequestParam(required = false, value =
	 * "category") String category, Model model, HttpSession session) {
	 * 
	 * model.addAttribute("title", "Home - Eworld");
	 * 
	 * try {
	 * 
	 * List<Category> categories = this.categoryService.getAllCategories();
	 * model.addAttribute("categories", categories);
	 * 
	 * if (category == null) { List<Product> products =
	 * this.productService.getAllProducts(); model.addAttribute("products",
	 * products); model.addAttribute("active", 0); } else { List<Product> products =
	 * this.productService.getProductByCategory(Integer.parseInt(category));
	 * model.addAttribute("products", products); model.addAttribute("active",
	 * Integer.parseInt(category)); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); session.setAttribute("message",
	 * new Msg("Something Went Wrong!!", "alert-danger")); }
	 * 
	 * return "index-2"; }
	 */

	@GetMapping("/")
	public String homePage(Model model, HttpSession session) {

		model.addAttribute("title", "Home - Eworld");

		try {

			Map<String, Product> highPriorityProducts = this.productPriorityService.getHighPriorityProducts();

			List<Category> highPriorityCategories = this.categoryPriorityService.getHighPrioCategories();

			List<Product> recentProducts = this.productService.get10RecentProducts(0, 10);

			List<Product> highSellingProducts = this.orderService.getTopSellingProductsInLast15Days(0, 10).toList();

			List<Product> mostRatedRecentProducts = this.productService.getMostRatedProductsOfRecentDates();

			List<Product> productsByHighDiscount = this.productService.getProductsByHighDiscount(0, 10);

			model.addAttribute("highPriorityProducts", highPriorityProducts)
					.addAttribute("highPriorityCategories", highPriorityCategories)
					.addAttribute("recentProducts", recentProducts)
					.addAttribute("highSellingProducts", highSellingProducts)
					.addAttribute("mostRatedRecentProducts", mostRatedRecentProducts)
					.addAttribute("productsByHighDiscount", productsByHighDiscount);

			/*
			 * params category, recent products, high discounted product, popular product,
			 * best selling product
			 */

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