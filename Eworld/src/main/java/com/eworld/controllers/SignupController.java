package com.eworld.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.eworld.entities.Cart;
import com.eworld.entities.User;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.UserService;

@Controller
public class SignupController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserService userService;
	
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
	
	@GetMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","Signup - Eworld");
		model.addAttribute("user", new User());
		
		return "signup";
		
	}
	
	@PostMapping("/do_signup")
	public String signupData(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, HttpSession session) {
		model.addAttribute("title", "Signup - Eworld");
		
		try {
			
			if(result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}
			
			if(this.userService.findByEmail(user.getEmail()) != null) {
				User oldUser = this.userService.findByEmail(user.getEmail());
				if(this.passwordEncoder.matches(user.getPassword(), oldUser.getPassword()) && oldUser.getMobilenum().equals(user.getMobilenum())) {
					
					session.setAttribute("message", new Msg("You Are Already Registered. Please Login", "alert-success"));
					
					return "redirect:/login";
					
				}
				else {
					
					session.setAttribute("message", new Msg("Fill valid Password or Mobile Number", "alert-danger"));
					model.addAttribute("user", user);
					return "signup";
					
				}
			}
			
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
			user.setRole("ROLE_USER");
			user.setProfilePic("default_profile.png");
			
			this.userService.saveUser(user);
			
			session.setAttribute("message", new Msg("You Are Successfully Registered", "alert-success"));
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}
		
		return "redirect:/login";
		
	}
	
}
