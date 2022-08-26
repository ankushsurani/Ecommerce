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

import com.eworld.entities.Cart;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.helper.Msg;
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
	
	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if(principal != null) {
			User user = this.userService.findByEmail(principal.getName());
				model.addAttribute("currentUser", user);
				
			List<Cart> carts = this.cartService.findByUser(user);
			model.addAttribute("cart",carts);
		}
	}
	
	@GetMapping("/product/{pId}")
	public String getProduct(@PathVariable("pId") int pId ,Model model, HttpSession session) {
		
		try {
			
			Product product = this.productService.getProduct(pId);
			model.addAttribute("product", product);
			model.addAttribute("title",product.getpName());
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}
		
		return "product_details";
	}

}
