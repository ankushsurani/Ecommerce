package com.eworld.controllers.user;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.helper.EmailService;
import com.eworld.helper.Msg;
import com.eworld.services.AddressService;
import com.eworld.services.CartService;
import com.eworld.services.OrderService;
import com.eworld.services.UserService;
import com.eworld.validation.EditProfileValidation;

@Controller
@RequestMapping("/user")
public class MyProfileController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

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
			model.addAttribute("cart", carts)
			.addAttribute("user", user);
		}
	}

	// show account
	@GetMapping("/account")
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

		return "user/account/account-dashboard";
	}
	
	@GetMapping("/account/orders")
	public String accountOrders(Principal principal, Model model) {
		List<AccountOrderDto> orders = this.orderService.getOrderByUser(principal.getName());

		model.addAttribute("orders", orders);
		
		return "user/account/account-orders";
	}
	
	@GetMapping("/account/profile")
	public String accountProfile() {
		return "user/account/account-profile";
	}

	@GetMapping("/account/edit-profile")
	public String accountEditProfile() {
		return "user/account/account-edit-profile";
	}
	
	// update user account details
	@PostMapping("/account/edit-account-details")
	public String updateAccountDetails(@Validated(EditProfileValidation.class) @ModelAttribute("user") User user, BindingResult result, Model model, HttpSession session) {
		model.addAttribute("title", "Update Profile - Eworld");

		try {
			
			if (result.hasErrors()) {
				model.addAttribute("user", user);
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
	
	@GetMapping("/account/saved-address")
	public String savedAddress() {
		return "user/account/account-saved-address";
	}
	
	// add address
	@PostMapping("/account/add-address")
	public String addAddress(@Valid @ModelAttribute("address") Address address, BindingResult result, Model model,
			Principal principal, HttpSession session) {
		
		model.addAttribute("title", "Add Address - Eworld");

			try {

				if (result.hasErrors()) {
					model.addAttribute("address", address);
					return "redirect:/user/account/account-saved-address";
				}

				User user = this.userService.findByEmail(principal.getName());

				address.setUser(user);
				this.addressService.saveAddress(address);

				session.setAttribute("message", new Msg("Address Added Successfully", "alert-success"));

			} catch (Exception e) {
				e.printStackTrace();
				session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
			}

			return "redirect:/user/account/saved-address";

	}
	
	@GetMapping("/account/wishlist")
	public String wishlist() {
		return "user/account/wishlist";
	}
	
	// add address
	@PostMapping("/add-address")
	public String addAddress(@Valid @ModelAttribute("address") Address address, BindingResult result, Model model,
			Principal principal, HttpSession session,
			@RequestParam(name = "paymentpage", required = false) String paymentPage) {
		model.addAttribute("title", "Add Address - Eworld");

		if (paymentPage != null && paymentPage.equals("yes")) {
			try {

				if (result.hasErrors()) {
					model.addAttribute("address", address);
					session.setAttribute("message", new Msg("Please Fill Valid Address Details", "alert-success"));
					return "user/place_order";
				}

				User user = this.userService.findByEmail(principal.getName());

				address.setUser(user);
				this.addressService.saveAddress(address);

				session.setAttribute("message", new Msg("Address Added Successfully", "alert-success"));

			} catch (Exception e) {
				e.printStackTrace();
				session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
			}

			return "redirect:/user/mycart";
		} else {

			try {

				if (result.hasErrors()) {
					model.addAttribute("address", address);
					session.setAttribute("message", new Msg("Please Fill Valid Address Details", "alert-success"));
					return "redirect:/user/account";
				}

				User user = this.userService.findByEmail(principal.getName());

				address.setUser(user);
				this.addressService.saveAddress(address);

				session.setAttribute("message", new Msg("Address Added Successfully", "alert-success"));

			} catch (Exception e) {
				e.printStackTrace();
				session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
			}

			return "redirect:/user/account";

		}

	}

	// edit address
	@PostMapping("/edit-address/{addressId}")
	public String editAddress(@PathVariable("addressId") String addressId, Model model, Principal principal,
			HttpSession session) {
		model.addAttribute("title", "Edit Address - Eworld");

		try {

			Address address = this.addressService.getAddressById(addressId);

			model.addAttribute("editAddress", address);
			model.addAttribute("address", new Address());

			User user = this.userService.findByEmail(principal.getName());
			List<Address> addresses = this.addressService.getAddressByUser(user);
			model.addAttribute("addresses", addresses);

			model.addAttribute("edit", true);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "user/account";
	}

	// delete address
	@PostMapping("/delete-address/{addressId}")
	public String deleteAddress(@PathVariable("addressId") String addressId, Model model, HttpSession session) {
		model.addAttribute("title", "Delete Address - Eworld");

		try {

			Address address = this.addressService.getAddressById(addressId);

			this.addressService.deleteAddress(address);
			session.setAttribute("message", new Msg("Your Address is Deleted Successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/account";
	}

	// update user name details
	@PostMapping("/update-name")
	public String updateName(@ModelAttribute("user") User user, Model model, HttpSession session) {
		model.addAttribute("title", "Update Profile - Eworld");

		try {

			User oldUser = this.userService.getUserById(user.getId());

			if (user.getFullName() != null) {
				oldUser.setFullName(user.getFullName());
			}
			if (user.getGender() != null) {
				oldUser.setGender(user.getGender());
			}

			this.userService.saveUser(oldUser);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/account";

	}

	// update user mobile number details
	@PostMapping("/update-mobile")
	public String updateNumber(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
			HttpSession session) {
		model.addAttribute("title", "Update Profile - Eworld");

		try {

			if (result.hasErrors()) {
				session.setAttribute("message", new Msg("Plese Enter Valid 10 Digits Mobile Number", "alert-danger"));
				return "redirect:/user/account";
			}

			User oldUser = this.userService.getUserById(user.getId());

			oldUser.setMobilenum(user.getMobilenum());

			this.userService.saveUser(oldUser);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/user/account";

	}

	// show change password page
	@GetMapping("/change-password")
	public String showChangePassword(Model model) {
		model.addAttribute("title", "Change Password - Eworld");

		return "user/change_password";
	}

	// change user password
	@PostMapping("/change-password/{userId}")
	public String changePassword(@PathVariable("userId") String userId, Model model, HttpSession session) {
		model.addAttribute("title", "Change Password - Eworld");

		try {

			User user = this.userService.getUserById(userId);

			int int1 = otp1.nextInt(3);
			int int2 = otp2.nextInt(4);
			int int3 = otp3.nextInt(8);
			int int4 = otp4.nextInt(9);
			int int5 = otp5.nextInt(9);
			int int6 = otp6.nextInt(6);

			int fullOtp = int1 + int2 * 10 + int3 * 100 + int4 * 1000 + int5 * 10000 + int6 * 100000;

			String subject = "Eworld - Shop Your Fabs";

			String to = user.getEmail();

			String message = "<!DOCTYPE html>\r\n"
					+ "<html xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"en\">\r\n"
					+ "\r\n" + "<head>\r\n" + "	<title></title>\r\n"
					+ "	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n"
					+ "	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
					+ "	<!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]-->\r\n"
					+ "	<style>\r\n" + "		* {\r\n" + "			box-sizing: border-box;\r\n" + "		}\r\n"
					+ "\r\n" + "		body {\r\n" + "			margin: 0;\r\n" + "			padding: 0;\r\n"
					+ "		}\r\n" + "\r\n" + "		a[x-apple-data-detectors] {\r\n"
					+ "			color: inherit !important;\r\n" + "			text-decoration: inherit !important;\r\n"
					+ "		}\r\n" + "\r\n" + "		#MessageViewBody a {\r\n" + "			color: inherit;\r\n"
					+ "			text-decoration: none;\r\n" + "		}\r\n" + "\r\n" + "		p {\r\n"
					+ "			line-height: inherit\r\n" + "		}\r\n" + "\r\n" + "		.desktop_hide,\r\n"
					+ "		.desktop_hide table {\r\n" + "			mso-hide: all;\r\n" + "			display: none;\r\n"
					+ "			max-height: 0px;\r\n" + "			overflow: hidden;\r\n" + "		}\r\n" + "\r\n"
					+ "		@media (max-width:620px) {\r\n" + "			.desktop_hide table.icons-inner {\r\n"
					+ "				display: inline-block !important;\r\n" + "			}\r\n" + "\r\n"
					+ "			.icons-inner {\r\n" + "				text-align: center;\r\n" + "			}\r\n"
					+ "\r\n" + "			.icons-inner td {\r\n" + "				margin: 0 auto;\r\n"
					+ "			}\r\n" + "\r\n" + "			.fullMobileWidth,\r\n"
					+ "			.image_block img.big,\r\n" + "			.row-content {\r\n"
					+ "				width: 100% !important;\r\n" + "			}\r\n" + "\r\n"
					+ "			.mobile_hide {\r\n" + "				display: none;\r\n" + "			}\r\n" + "\r\n"
					+ "			.stack .column {\r\n" + "				width: 100%;\r\n"
					+ "				display: block;\r\n" + "			}\r\n" + "\r\n" + "			.mobile_hide {\r\n"
					+ "				min-height: 0;\r\n" + "				max-height: 0;\r\n"
					+ "				max-width: 0;\r\n" + "				overflow: hidden;\r\n"
					+ "				font-size: 0px;\r\n" + "			}\r\n" + "\r\n" + "			.desktop_hide,\r\n"
					+ "			.desktop_hide table {\r\n" + "				display: table !important;\r\n"
					+ "				max-height: none !important;\r\n" + "			}\r\n" + "		}\r\n"
					+ "	</style>\r\n" + "</head>\r\n" + "\r\n"
					+ "<body style=\"background-color: #FFFFFF; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\r\n"
					+ "	<table class=\"nl-container\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #FFFFFF;\">\r\n"
					+ "		<tbody>\r\n" + "			<tr>\r\n" + "				<td>\r\n"
					+ "					<table class=\"row row-1\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #132437;\">\r\n"
					+ "						<tbody>\r\n" + "							<tr>\r\n"
					+ "								<td>\r\n"
					+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-position: center top; background-repeat: no-repeat; background-image: url('https://d1oco4z2z1fhwp.cloudfront.net/templates/default/4016/blue-glow_3.jpg'); color: #000000; width: 600px;\" width=\"600\">\r\n"
					+ "										<tbody>\r\n"
					+ "											<tr>\r\n"
					+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
					+ "													<table class=\"image_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"padding-bottom:35px;padding-left:30px;padding-right:30px;padding-top:35px;width:100%;\">\r\n"
					+ "																<div class=\"alignment\" align=\"center\" style=\"line-height:10px\"><img src=\"https://d15k2d11r6t6rl.cloudfront.net/public/users/Integrators/BeeProAgency/850389_834438/editor_images/54bcf660-f08f-4dc8-8f66-8c6612da8456.png\" style=\"display: block; height: auto; border: 0; width: 240px; max-width: 100%;\" width=\"240\" alt=\"Books Plus\" title=\"Books Plus\"></div>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "													<table class=\"image_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"width:100%;padding-right:0px;padding-left:0px;\">\r\n"
					+ "																<div class=\"alignment\" align=\"center\" style=\"line-height:10px\"><img class=\"fullMobileWidth\" src=\"https://d1oco4z2z1fhwp.cloudfront.net/templates/default/4016/top-rounded.png\" style=\"display: block; height: auto; border: 0; width: 600px; max-width: 100%;\" width=\"600\"></div>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "												</td>\r\n"
					+ "											</tr>\r\n"
					+ "										</tbody>\r\n"
					+ "									</table>\r\n" + "								</td>\r\n"
					+ "							</tr>\r\n" + "						</tbody>\r\n"
					+ "					</table>\r\n"
					+ "					<table class=\"row row-2\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #132437;\">\r\n"
					+ "						<tbody>\r\n" + "							<tr>\r\n"
					+ "								<td>\r\n"
					+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-position: center top; background-color: #ffffff; color: #000000; width: 600px;\" width=\"600\">\r\n"
					+ "										<tbody>\r\n"
					+ "											<tr>\r\n"
					+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 10px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
					+ "													<table class=\"image_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"padding-bottom:5px;padding-left:20px;padding-right:20px;padding-top:5px;width:100%;\">\r\n"
					+ "																<div class=\"alignment\" align=\"center\" style=\"line-height:10px\"><img class=\"big\" src=\"https://d15k2d11r6t6rl.cloudfront.net/public/users/Integrators/BeeProAgency/850389_834438/iStock-984796804.webp\" style=\"display: block; height: auto; border: 0; width: 560px; max-width: 100%;\" width=\"560\" alt=\"book shelf\" title=\"book shelf\"></div>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "												</td>\r\n"
					+ "											</tr>\r\n"
					+ "										</tbody>\r\n"
					+ "									</table>\r\n" + "								</td>\r\n"
					+ "							</tr>\r\n" + "						</tbody>\r\n"
					+ "					</table>\r\n"
					+ "					<table class=\"row row-3\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #ff7d14; background-image: url('https://d1oco4z2z1fhwp.cloudfront.net/templates/default/4016/orange-gradient-wide.png'); background-repeat: no-repeat;\">\r\n"
					+ "						<tbody>\r\n" + "							<tr>\r\n"
					+ "								<td>\r\n"
					+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #ffffff; color: #000000; width: 600px;\" width=\"600\">\r\n"
					+ "										<tbody>\r\n"
					+ "											<tr>\r\n"
					+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
					+ "													<table class=\"heading_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"padding-bottom:5px;padding-top:25px;text-align:center;width:100%;\">\r\n"
					+ "																<h1 style=\"margin: 0; color: #555555; direction: ltr; font-family: Arial, Helvetica Neue, Helvetica, sans-serif; font-size: 36px; font-weight: 400; letter-spacing: normal; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"><strong>Welcome to Eworld</strong></h1>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "													<table class=\"text_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"padding-bottom:20px;padding-left:30px;padding-right:30px;padding-top:20px;\">\r\n"
					+ "																<div style=\"font-family: sans-serif\">\r\n"
					+ "																	<div class=\"txtTinyMce-wrapper\" style=\"font-size: 14px; mso-line-height-alt: 25.2px; color: #737487; line-height: 1.8; font-family: Arial, Helvetica Neue, Helvetica, sans-serif;\">\r\n"
					+ "																		<p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 32.4px;\"><span style=\"font-size:18px;\">Use Code "
					+ fullOtp + " To Reset Your Eworld Password. Do Not Share This Code With Anyone.</span></p>\r\n"
					+ "																	</div>\r\n"
					+ "																</div>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "												</td>\r\n"
					+ "											</tr>\r\n"
					+ "										</tbody>\r\n"
					+ "									</table>\r\n" + "								</td>\r\n"
					+ "							</tr>\r\n" + "						</tbody>\r\n"
					+ "					</table>\r\n"
					+ "					<table class=\"row row-4\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #ff7d14;\">\r\n"
					+ "						<tbody>\r\n" + "							<tr>\r\n"
					+ "								<td>\r\n"
					+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #ffffff; color: #000000; width: 600px;\" width=\"600\">\r\n"
					+ "										<tbody>\r\n"
					+ "											<tr>\r\n"
					+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
					+ "													<table class=\"heading_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"padding-bottom:5px;padding-top:25px;text-align:center;width:100%;\">\r\n"
					+ "																<h2 style=\"margin: 0; color: #555555; direction: ltr; font-family: Arial, Helvetica Neue, Helvetica, sans-serif; font-size: 24px; font-weight: 400; letter-spacing: normal; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"><strong>Your Shopping, online and off</strong></h2>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "													<table class=\"text_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"padding-bottom:20px;padding-left:30px;padding-right:30px;padding-top:20px;\">\r\n"
					+ "																<div style=\"font-family: sans-serif\">\r\n"
					+ "																	<div class=\"txtTinyMce-wrapper\" style=\"font-size: 14px; mso-line-height-alt: 25.2px; color: #737487; line-height: 1.8; font-family: Arial, Helvetica Neue, Helvetica, sans-serif;\">\r\n"
					+ "																		<p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 32.4px;\"><span style=\"font-size:18px;\">You now get unlimited offers on our Products.</span></p>\r\n"
					+ "																	</div>\r\n"
					+ "																</div>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "													<table class=\"image_block block-3\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"padding-bottom:35px;padding-top:10px;width:100%;padding-right:0px;padding-left:0px;\">\r\n"
					+ "																<div class=\"alignment\" align=\"center\" style=\"line-height:10px\"><img class=\"big\" src=\"https://d1oco4z2z1fhwp.cloudfront.net/templates/default/4016/divider.png\" style=\"display: block; height: auto; border: 0; width: 541px; max-width: 100%;\" width=\"541\"></div>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "													<table class=\"text_block block-4\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\">\r\n"
					+ "																<div style=\"font-family: sans-serif\">\r\n"
					+ "																	<div class=\"txtTinyMce-wrapper\" style=\"font-size: 14px; mso-line-height-alt: 25.2px; color: #07113e; line-height: 1.8; font-family: Arial, Helvetica Neue, Helvetica, sans-serif;\">\r\n"
					+ "																		<p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 32.4px;\"><span style=\"font-size:18px;\">Follow us</span></p>\r\n"
					+ "																	</div>\r\n"
					+ "																</div>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "													<table class=\"social_block block-5\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"padding-bottom:15px;padding-left:15px;padding-right:15px;padding-top:10px;text-align:center;\">\r\n"
					+ "																<div class=\"alignment\" style=\"text-align:center;\">\r\n"
					+ "																	<table class=\"social-table\" width=\"138px\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; display: inline-block;\">\r\n"
					+ "																		<tr>\r\n"
					+ "																			<td style=\"padding:0 7px 0 7px;\"><a href=\"https://www.facebook.com/\" target=\"_blank\"><img src=\"https://app-rsrc.getbee.io/public/resources/social-networks-icon-sets/circle-dark-gray/facebook@2x.png\" width=\"32\" height=\"32\" alt=\"Facebook\" title=\"Facebook\" style=\"display: block; height: auto; border: 0;\"></a></td>\r\n"
					+ "																			<td style=\"padding:0 7px 0 7px;\"><a href=\"https://twitter.com/\" target=\"_blank\"><img src=\"https://app-rsrc.getbee.io/public/resources/social-networks-icon-sets/circle-dark-gray/twitter@2x.png\" width=\"32\" height=\"32\" alt=\"Twitter\" title=\"Twitter\" style=\"display: block; height: auto; border: 0;\"></a></td>\r\n"
					+ "																			<td style=\"padding:0 7px 0 7px;\"><a href=\"https://instagram.com/\" target=\"_blank\"><img src=\"https://app-rsrc.getbee.io/public/resources/social-networks-icon-sets/circle-dark-gray/instagram@2x.png\" width=\"32\" height=\"32\" alt=\"Instagram\" title=\"Instagram\" style=\"display: block; height: auto; border: 0;\"></a></td>\r\n"
					+ "																		</tr>\r\n"
					+ "																	</table>\r\n"
					+ "																</div>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "												</td>\r\n"
					+ "											</tr>\r\n"
					+ "										</tbody>\r\n"
					+ "									</table>\r\n" + "								</td>\r\n"
					+ "							</tr>\r\n" + "						</tbody>\r\n"
					+ "					</table>\r\n"
					+ "					<table class=\"row row-5\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "						<tbody>\r\n" + "							<tr>\r\n"
					+ "								<td>\r\n"
					+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000000; width: 600px;\" width=\"600\">\r\n"
					+ "										<tbody>\r\n"
					+ "											<tr>\r\n"
					+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
					+ "													<table class=\"icons_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "														<tr>\r\n"
					+ "															<td class=\"pad\" style=\"vertical-align: middle; color: #9d9d9d; font-family: inherit; font-size: 15px; padding-bottom: 5px; padding-top: 5px; text-align: center;\">\r\n"
					+ "																<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
					+ "																	<tr>\r\n"
					+ "																		<td class=\"alignment\" style=\"vertical-align: middle; text-align: center;\">\r\n"
					+ "																			<!--[if vml]><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"display:inline-block;padding-left:0px;padding-right:0px;mso-table-lspace: 0pt;mso-table-rspace: 0pt;\"><![endif]-->\r\n"
					+ "																			<!--[if !vml]><!-->\r\n"
					+ "																			<table class=\"icons-inner\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; display: inline-block; margin-right: -4px; padding-left: 0px; padding-right: 0px;\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\r\n"
					+ "																				<!--<![endif]-->\r\n"
					+ "																				<tr>\r\n"
					+ "																					<td style=\"vertical-align: middle; text-align: center; padding-top: 5px; padding-bottom: 5px; padding-left: 5px; padding-right: 6px;\"><a href=\"https://www.designedwithbee.com/?utm_source=editor&utm_medium=bee_pro&utm_campaign=free_footer_link\" target=\"_blank\" style=\"text-decoration: none;\"><img class=\"icon\" alt=\"Designed with BEE\" src=\"https://d15k2d11r6t6rl.cloudfront.net/public/users/Integrators/BeeProAgency/53601_510656/Signature/bee.png\" height=\"32\" width=\"34\" align=\"center\" style=\"display: block; height: auto; margin: 0 auto; border: 0;\"></a></td>\r\n"
					+ "																					<td style=\"font-family: Arial, Helvetica Neue, Helvetica, sans-serif; font-size: 15px; color: #9d9d9d; vertical-align: middle; letter-spacing: undefined; text-align: center;\"><a href=\"https://www.designedwithbee.com/?utm_source=editor&utm_medium=bee_pro&utm_campaign=free_footer_link\" target=\"_blank\" style=\"color: #9d9d9d; text-decoration: none;\">Designed with BEE</a></td>\r\n"
					+ "																				</tr>\r\n"
					+ "																			</table>\r\n"
					+ "																		</td>\r\n"
					+ "																	</tr>\r\n"
					+ "																</table>\r\n"
					+ "															</td>\r\n"
					+ "														</tr>\r\n"
					+ "													</table>\r\n"
					+ "												</td>\r\n"
					+ "											</tr>\r\n"
					+ "										</tbody>\r\n"
					+ "									</table>\r\n" + "								</td>\r\n"
					+ "							</tr>\r\n" + "						</tbody>\r\n"
					+ "					</table>\r\n" + "				</td>\r\n" + "			</tr>\r\n"
					+ "		</tbody>\r\n" + "	</table><!-- End -->\r\n" + "</body>\r\n" + "\r\n" + "</html>";

			this.emailService.sendEmail(subject, message, to);

			model.addAttribute("otp", fullOtp);

			session.setAttribute("message", new Msg("OTP Successfully Sent To " + user.getEmail(), "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "user/change_password";

	}

	// change password process
	@PostMapping("/change-password-process")
	public String changePasswordProcess(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("retypePassword") String retypePassword,
			@RequestParam("myOtp") int myOtp, @RequestParam("sendedOtp") int sendedOtp, HttpSession session,
			Model model, Principal principal) {
		model.addAttribute("title", "Change Password Process - Eworld");

		try {

			User user = this.userService.findByEmail(principal.getName());

			if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {

				if (newPassword.equals(retypePassword)) {

					if (myOtp == sendedOtp) {
						user.setPassword(this.passwordEncoder.encode(newPassword));
						this.userService.saveUser(user);

						session.setAttribute("message",
								new Msg("Your Password Has Been Successfully Changed", "alert-success"));
						return "redirect:/user/account";
					} else {
						session.setAttribute("message", new Msg("Please Enter Valid OTP", "alert-danger"));
						model.addAttribute("otp", sendedOtp);
						return "user/change_password";
					}

				} else {
					session.setAttribute("message", new Msg("Both New Passwords Must Be Same", "alert-danger"));
					model.addAttribute("otp", sendedOtp);
					return "user/change_password";
				}

			} else {
				session.setAttribute("message", new Msg("Please Enter Correct Password", "alert-danger"));
				model.addAttribute("otp", sendedOtp);
				return "user/change_password";
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
			return "redirect:/user/account";
		}

	}

}
