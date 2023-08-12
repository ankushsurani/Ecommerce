package com.eworld.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.eworld.entities.Cart;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.OrderService;
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
	private OrderService orderService;
	
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
	public String filterProducts(@RequestParam(name = "sort", required = false) String sort,
			@PathVariable("pageNum") int pageNum, Model model) {

		List<Product> products = null;
		
		if (sort != null) {
			switch (sort) {
			case "latest": {
				products = this.productService.getLatestProducts(pageNum, this.pageSize);
				break;
			}
			case "rating":
				products = this.productService.getMostRatedProducts(pageNum, this.pageSize);
				break;
			case "popularity":
				products = this.orderService.getPopularProducts(pageNum, this.pageSize);
				break;
			case "highDiscount":
				products = this.productService.getProductsByHighDiscount(0, this.pageSize);
				break;
			}
		} else {
			products = this.productService.getAllProducts(pageNum, this.pageSize);
		}
		
		model.addAttribute("products", products);

		return "products";
	}

}