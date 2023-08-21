package com.eworld.controllers;

import java.security.Principal;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.eworld.dto.FilterRequest;
import com.eworld.entities.Cart;
import com.eworld.entities.Category;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.services.CartService;
import com.eworld.services.CategoryService;
import com.eworld.services.ProductService;
import com.eworld.services.UserService;

@Controller
public class ProductController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CartService cartService;

	@Autowired
	private CategoryService categoryService;

	@Value("${spring.application.name}")
	private String appName;

	private int pageSize = 12;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());
			model.addAttribute("currentUser", user);

			List<Cart> carts = this.cartService.findByUser(user);
			model.addAttribute("cart", carts);
		}
		model.addAttribute("appName", this.appName);
		model.addAttribute("subPageName", "Products");

		List<Category> categories = this.categoryService.getAllCategories();
		model.addAttribute("categories", categories);
	}

	@GetMapping("/product/page={pageNum}")
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
			model.addAttribute("allBrandName", allBrandName);

		} catch (Exception e) {
			System.out.println("Something Went Wrong");
		}

		return "products";
	}

}