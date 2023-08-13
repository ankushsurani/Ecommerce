package com.eworld.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.eworld.entities.Cart;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.helper.FilterRequest;
import com.eworld.services.CartService;
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

	private int pageSize = 12;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());
			model.addAttribute("currentUser", user);

			List<Cart> carts = this.cartService.findByUser(user);
			model.addAttribute("cart", carts);
		}
	}

	@GetMapping("/product/page={pageNum}")
	public String filterProducts(@PathVariable("pageNum") int pageNum,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "categoryId", required = false) Integer categoryId,
			@RequestParam(name = "minPrice", required = false) Integer minPrice,
			@RequestParam(name = "maxPrice", required = false) Integer maxPrice,
			@RequestParam(name = "brandName", required = false) String brandName, Model model) {

		try {

			FilterRequest filterRequest = new FilterRequest(sortBy, categoryId, minPrice, maxPrice, brandName);

			System.out.println(filterRequest);

			Pageable pageable = PageRequest.of(pageNum, this.pageSize);

			Page<Product> products = productService.getFilteredAndSortedProducts(filterRequest, pageable);

			model.addAttribute("products", products.getContent());

		} catch (Exception e) {
			System.out.println("Something Went Wrong");
		}

		return "products";
	}

}