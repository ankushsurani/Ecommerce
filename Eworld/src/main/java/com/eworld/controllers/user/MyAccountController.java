package com.eworld.controllers.user;

import java.security.Principal;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eworld.dto.AccountOrderDto;
import com.eworld.entities.Address;
import com.eworld.entities.Cart;
import com.eworld.entities.User;
import com.eworld.helper.Msg;
import com.eworld.services.AddressService;
import com.eworld.services.CartService;
import com.eworld.services.OrderService;
import com.eworld.services.UserService;
import com.eworld.validation.EditProfileValidation;

@Controller
@RequestMapping("/user/account")
public class MyAccountController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Value("${spring.application.name}")
	private String appName;

	Random otp1 = new Random(1);
	Random otp2 = new Random(1);
	Random otp3 = new Random(5);
	Random otp4 = new Random(8);
	Random otp5 = new Random(7);
	Random otp6 = new Random(3);

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());

			List<Cart> carts = this.cartService.findByUser(user);
			model.addAttribute("cart", carts).addAttribute("user", user);
		}

		User user = this.userService.findByEmail(principal.getName());

		boolean haveAddress = user.getAddress().stream().anyMatch(address -> address.isActive());

		model.addAttribute("haveAddress", haveAddress);
		model.addAttribute("appName", this.appName);
		model.addAttribute("subPageName", "My Account");

	}

	// show account
	@GetMapping("/dashboard")
	public String showProfile(Model model, Principal principal, HttpSession session) {
		model.addAttribute("title", "My Profile - Eworld");
		model.addAttribute("address", new Address());

		try {

			User user = this.userService.findByEmail(principal.getName());
			List<Address> addresses = this.addressService.getAddressByUser(user);
			model.addAttribute("addresses", addresses);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}
		
		model.addAttribute("pageName", "Dashboard");

		return "user/account/account-dashboard";
	}

	@GetMapping("/orders")
	public String accountOrders(Principal principal, Model model) {
		List<AccountOrderDto> orders = this.orderService.getOrderByUser(principal.getName());

		model.addAttribute("orders", orders);
		
		model.addAttribute("pageName", "Orders");

		return "user/account/account-orders";
	}

	@GetMapping("/profile")
	public String accountProfile(Model model) {
		model.addAttribute("pageName", "Profile");
		return "user/account/account-profile";
	}

	@GetMapping("/edit-profile")
	public String accountEditProfile(Model model) {
		model.addAttribute("pageName", "Edit Profile");
		return "user/account/account-edit-profile";
	}

	// update user account details
	@PostMapping("/edit-account-details")
	public String updateAccountDetails(@Validated(EditProfileValidation.class) @ModelAttribute("user") User user,
			BindingResult result, Model model, HttpSession session) {
		model.addAttribute("title", "Update Profile - Eworld");

		try {

			if (result.hasErrors()) {
				model.addAttribute("user", user);
				model.addAttribute("pageName", "Edit Profile");
				return "user/account/account-edit-profile";
			}

			User existingUser = this.userService.getUserById(user.getId());

			existingUser.setFullName(user.getFullName());

			if (user.getMobilenum() != null) {
				existingUser.setMobilenum(user.getMobilenum());
			}
			if (user.getDob() != null) {
				existingUser.setDob(user.getDob());
			}
			if (user.getGender() != null) {
				existingUser.setGender(user.getGender());
			}

			this.userService.saveUser(existingUser);

			session.setAttribute("message", new Msg("Account Details Changed Successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/account/edit-profile";

	}

	@GetMapping("/saved-address")
	public String savedAddress(Model model) {
		model.addAttribute("address", new Address());
		model.addAttribute("pageName", "Saved Address");
		return "user/account/account-saved-address";
	}

	// add address
	@PostMapping("/add-address")
	public String addAddress(@Valid @ModelAttribute("address") Address address, BindingResult result, Model model,
			Principal principal, HttpSession session) {

		model.addAttribute("title", "Add Address - Eworld");

		try {

			if (result.hasErrors()) {
				model.addAttribute("address", address);
				model.addAttribute("pageName", "Saved Address");
				return "/user/account/account-saved-address";
			}

			User user = this.userService.findByEmail(principal.getName());

			address.setUser(user);

			if (!this.userService.hasAnyActiveAddress(user.getId())) {
				address.setAddressType("MAIN");
			} else {
				address.setAddressType("OTHER");
			}

			this.addressService.saveAddress(address);

			session.setAttribute("message", new Msg("Address Added Successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/account/saved-address";

	}

	/*
	 * @PostMapping("/account/edit-address/{addressId}") public String
	 * editAddress(@Valid @ModelAttribute("address") Address address, BindingResult
	 * result, @PathVariable("addressId")String addressId, Model model, Principal
	 * principal, HttpSession session) {
	 * 
	 * model.addAttribute("title", "Edit Address - Eworld");
	 * 
	 * try {
	 * 
	 * address.setId(addressId);
	 * 
	 * if (result.hasErrors()) { model.addAttribute("address", address); return
	 * "/user/account/account-saved-address"; }
	 * 
	 * this.addressService.saveAddress(address); session.setAttribute("message", new
	 * Msg("Address Updated Successfully", "alert-success"));
	 * 
	 * } catch (Exception e) { e.printStackTrace(); session.setAttribute("message",
	 * new Msg("Something Went Wrong!!", "alert-danger")); }
	 * 
	 * return "redirect:/user/account/saved-address"; }
	 */

	// delete address
	@PostMapping("/delete-address/{addressId}")
	public String deleteAddress(@PathVariable("addressId") String addressId, Model model, HttpSession session,
			Principal principal) {
		model.addAttribute("title", "Delete Address - Eworld");

		try {

			Address address = this.addressService.getAddressById(addressId);
			User user = this.userService.findByEmail(principal.getName());

			boolean anyOrderWithThisAddress = this.orderService.anyOrderWithThisAddress(address, user);

			if (!anyOrderWithThisAddress) {
				this.addressService.deleteAddress(addressId);
			} else {
				address.setActive(false);
				this.addressService.saveAddress(address);
			}

			session.setAttribute("message", new Msg("Your Address is Deleted Successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/account/saved-address";
	}

	@GetMapping("/wishlist")
	public String wishlist(Model model) {
		model.addAttribute("pageName", "Wishlist");
		return "user/account/wishlist";
	}

	// change password
	@PostMapping("/change-password-process")
	public String changePasswordProcess(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("retypePassword") String retypePassword,
			HttpSession session, Model model, Principal principal) {
		model.addAttribute("title", "Change Password - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {

				if (newPassword.equals(retypePassword)) {
					user.setPassword(this.passwordEncoder.encode(newPassword));
					this.userService.saveUser(user);

					session.setAttribute("message",
							new Msg("Your Password Has Been Successfully Changed", "alert-success"));
					return "redirect:/user/account/edit-profile";
				} else {
					session.setAttribute("message", new Msg("Both New Passwords Must Be Same", "alert-danger"));
					model.addAttribute("pageName", "Edit Profile");
					return "user/account/account-edit-profile";
				}

			} else {
				session.setAttribute("message", new Msg("Please Enter Correct Password", "alert-danger"));
				model.addAttribute("pageName", "Edit Profile");
				return "user/account/account-edit-profile";
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
			return "user/account/account-edit-profile";
		}

	}

}
