package com.eworld.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eworld.entities.CartItem;
import com.eworld.entities.User;
import com.eworld.entities.WishlistItem;
import com.eworld.helper.EmailService;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.UserService;
import com.eworld.services.WishlistItemService;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private WishlistItemService wishlistItemService;

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
		model.addAttribute("subPageName", "Login");
		model.addAttribute("pageName", "My Account");
	}

	@GetMapping("/login")
	public String signup(@ModelAttribute("user") User user, Model model) {

		model.addAttribute("title", "Login - Eworld");

		return "login";

	}

	@GetMapping("/forgot")
	public String forgotPassword(Model model) {

		model.addAttribute("title", "Forgot Password - Eworld");
		model.addAttribute("addOtp", false);

		return "/forgot_password";
	}

	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("forgot_email") String email, Model model, HttpSession session) {
		model.addAttribute("title", "Send OTP - Eworld");

		try {

			User user = this.userService.findByEmail(email);

			if (user != null) {

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
						+ "	<style>\r\n" + "		* {\r\n" + "			box-sizing: border-box;\r\n"
						+ "		}\r\n" + "\r\n" + "		body {\r\n" + "			margin: 0;\r\n"
						+ "			padding: 0;\r\n" + "		}\r\n" + "\r\n" + "		a[x-apple-data-detectors] {\r\n"
						+ "			color: inherit !important;\r\n"
						+ "			text-decoration: inherit !important;\r\n" + "		}\r\n" + "\r\n"
						+ "		#MessageViewBody a {\r\n" + "			color: inherit;\r\n"
						+ "			text-decoration: none;\r\n" + "		}\r\n" + "\r\n" + "		p {\r\n"
						+ "			line-height: inherit\r\n" + "		}\r\n" + "\r\n" + "		.desktop_hide,\r\n"
						+ "		.desktop_hide table {\r\n" + "			mso-hide: all;\r\n"
						+ "			display: none;\r\n" + "			max-height: 0px;\r\n"
						+ "			overflow: hidden;\r\n" + "		}\r\n" + "\r\n"
						+ "		@media (max-width:620px) {\r\n" + "			.desktop_hide table.icons-inner {\r\n"
						+ "				display: inline-block !important;\r\n" + "			}\r\n" + "\r\n"
						+ "			.icons-inner {\r\n" + "				text-align: center;\r\n" + "			}\r\n"
						+ "\r\n" + "			.icons-inner td {\r\n" + "				margin: 0 auto;\r\n"
						+ "			}\r\n" + "\r\n" + "			.fullMobileWidth,\r\n"
						+ "			.image_block img.big,\r\n" + "			.row-content {\r\n"
						+ "				width: 100% !important;\r\n" + "			}\r\n" + "\r\n"
						+ "			.mobile_hide {\r\n" + "				display: none;\r\n" + "			}\r\n" + "\r\n"
						+ "			.stack .column {\r\n" + "				width: 100%;\r\n"
						+ "				display: block;\r\n" + "			}\r\n" + "\r\n"
						+ "			.mobile_hide {\r\n" + "				min-height: 0;\r\n"
						+ "				max-height: 0;\r\n" + "				max-width: 0;\r\n"
						+ "				overflow: hidden;\r\n" + "				font-size: 0px;\r\n"
						+ "			}\r\n" + "\r\n" + "			.desktop_hide,\r\n"
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

				model.addAttribute("forgotOtp", fullOtp);
				model.addAttribute("currentEmail", user.getEmail());

				session.setAttribute("message",
						new Msg("OTP Successfully Sent To " + user.getEmail(), "alert-success"));
				model.addAttribute("addOtp", true);

				return "forgot_password";
			}

			else {

				session.setAttribute("message", new Msg("Please Enter Valid Email Id", "alert-danger"));
				return "redirect:/forgot";

			}

		} catch (Exception e) {

			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
			return "redirect:/forgot";

		}

	}

	@PostMapping("/submit-forgot")
	public String submitForgotPassword(@RequestParam("currentEmail") String currentEmail,
			@RequestParam("forgot_otp") int forgot_otp, @RequestParam("forgotOtp") int sendedOtp,
			@RequestParam("newPass") String newPass, @RequestParam("retypePass") String retypePass, Model model,
			HttpSession session) {
		model.addAttribute("title", "Forgot Password - Eworld");

		try {

			User user = this.userService.findByEmail(currentEmail);

			if (newPass.equals(retypePass)) {

				if (forgot_otp == sendedOtp) {
					user.setPassword(this.passwordEncoder.encode(newPass));
					this.userService.saveUser(user);

					session.setAttribute("message",
							new Msg("Your Password Has Been Successfully Changed", "alert-success"));
					return "redirect:/login";
				} else {
					session.setAttribute("message", new Msg("Please Enter Valid OTP", "alert-danger"));
					model.addAttribute("forgotOtp", sendedOtp);
					return "forgot_password";
				}

			} else {
				session.setAttribute("message", new Msg("Both New Passwords Must Be Same", "alert-danger"));
				model.addAttribute("forgotOtp", sendedOtp);
				return "forgot_password";
			}

		} catch (Exception e) {

			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
			return "redirect:/forgot";

		}

	}

}