package com.eworld.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

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

import com.eworld.entities.CartItem;
import com.eworld.entities.User;
import com.eworld.entities.WishlistItem;
import com.eworld.helper.EmailService;
import com.eworld.helper.Msg;
import com.eworld.services.CartService;
import com.eworld.services.UserService;
import com.eworld.services.WishlistItemService;
import com.eworld.validation.SignupValidation;

@Controller
public class SignupController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private EmailService emailService;

	@Value("${spring.application.name}")
	private String appName;
	
	@Autowired
	private WishlistItemService wishlistItemService;

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
		model.addAttribute("subPageName", "Signup");
		model.addAttribute("pageName", "My Account");
	}

	@GetMapping("/signup")
	public String signup(Model model) {

		model.addAttribute("title", "Signup - Eworld");
		model.addAttribute("user", new User());

		return "signup";

	}

	@PostMapping("/do_signup")
	public String signupData(@Validated(SignupValidation.class) @ModelAttribute("user") User user, BindingResult result,
			Model model, HttpSession session) {
		model.addAttribute("title", "Signup - Eworld");

		try {

			if (result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}

			if (this.userService.findByEmail(user.getEmail()) != null) {
				User oldUser = this.userService.findByEmail(user.getEmail());
				if (this.passwordEncoder.matches(user.getPassword(), oldUser.getPassword())) {

					session.setAttribute("message",
							new Msg("You Are Already Registered. Please Login", "alert-success"));

					return "redirect:/login";

				} else {

					session.setAttribute("message", new Msg("Fill valid Password", "alert-danger"));
					model.addAttribute("user", user);
					return "signup";

				}
			}

			else {
				String subject = "Eworld - Shop Your Fabs";

				String to = user.getEmail();

				String emailVerificationId = UUID.randomUUID().toString();

				String verificationUrl = "http://localhost:8080/account-verification/verification="
						+ emailVerificationId;

				user.setEmailVerification(emailVerificationId);

				String message = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
						+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\r\n"
						+ "\r\n" + "<head>\r\n" + "  <!--[if gte mso 9]>\r\n" + "<xml>\r\n"
						+ "  <o:OfficeDocumentSettings>\r\n" + "    <o:AllowPNG/>\r\n"
						+ "    <o:PixelsPerInch>96</o:PixelsPerInch>\r\n" + "  </o:OfficeDocumentSettings>\r\n"
						+ "</xml>\r\n" + "<![endif]-->\r\n"
						+ "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n"
						+ "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
						+ "  <meta name=\"x-apple-disable-message-reformatting\">\r\n" + "  <!--[if !mso]><!-->\r\n"
						+ "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n" + "  <!--<![endif]-->\r\n"
						+ "  <title></title>\r\n" + "\r\n" + "  <style type=\"text/css\">\r\n"
						+ "    @media only screen and (min-width: 620px) {\r\n" + "      .u-row {\r\n"
						+ "        width: 600px !important;\r\n" + "      }\r\n" + "      .u-row .u-col {\r\n"
						+ "        vertical-align: top;\r\n" + "      }\r\n" + "      .u-row .u-col-100 {\r\n"
						+ "        width: 600px !important;\r\n" + "      }\r\n" + "    }\r\n" + "    \r\n"
						+ "    @media (max-width: 620px) {\r\n" + "      .u-row-container {\r\n"
						+ "        max-width: 100% !important;\r\n" + "        padding-left: 0px !important;\r\n"
						+ "        padding-right: 0px !important;\r\n" + "      }\r\n" + "      .u-row .u-col {\r\n"
						+ "        min-width: 320px !important;\r\n" + "        max-width: 100% !important;\r\n"
						+ "        display: block !important;\r\n" + "      }\r\n" + "      .u-row {\r\n"
						+ "        width: 100% !important;\r\n" + "      }\r\n" + "      .u-col {\r\n"
						+ "        width: 100% !important;\r\n" + "      }\r\n" + "      .u-col>div {\r\n"
						+ "        margin: 0 auto;\r\n" + "      }\r\n" + "    }\r\n" + "    \r\n" + "    body {\r\n"
						+ "      margin: 0;\r\n" + "      padding: 0;\r\n" + "    }\r\n" + "    \r\n" + "    table,\r\n"
						+ "    tr,\r\n" + "    td {\r\n" + "      vertical-align: top;\r\n"
						+ "      border-collapse: collapse;\r\n" + "    }\r\n" + "    \r\n" + "    p {\r\n"
						+ "      margin: 0;\r\n" + "    }\r\n" + "    \r\n" + "    .ie-container table,\r\n"
						+ "    .mso-container table {\r\n" + "      table-layout: fixed;\r\n" + "    }\r\n" + "    \r\n"
						+ "    * {\r\n" + "      line-height: inherit;\r\n" + "    }\r\n" + "    \r\n"
						+ "    a[x-apple-data-detectors='true'] {\r\n" + "      color: inherit !important;\r\n"
						+ "      text-decoration: none !important;\r\n" + "    }\r\n" + "    \r\n" + "    table,\r\n"
						+ "    td {\r\n" + "      color: #000000;\r\n" + "    }\r\n" + "    \r\n"
						+ "    #u_body a {\r\n" + "      color: #0000ee;\r\n" + "      text-decoration: underline;\r\n"
						+ "    }\r\n" + "  </style>\r\n" + "\r\n" + "\r\n" + "\r\n" + "  <!--[if !mso]><!-->\r\n"
						+ "  <link href=\"https://fonts.googleapis.com/css?family=Cabin:400,700\" rel=\"stylesheet\" type=\"text/css\">\r\n"
						+ "  <!--<![endif]-->\r\n" + "\r\n" + "</head>\r\n" + "\r\n"
						+ "<body class=\"clean-body u_body\" style=\"margin: 0;padding: 0;-webkit-text-size-adjust: 100%;background-color: #f9f9f9;color: #000000\">\r\n"
						+ "  <!--[if IE]><div class=\"ie-container\"><![endif]-->\r\n"
						+ "  <!--[if mso]><div class=\"mso-container\"><![endif]-->\r\n"
						+ "  <table id=\"u_body\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;min-width: 320px;Margin: 0 auto;background-color: #f9f9f9;width:100%\" cellpadding=\"0\" cellspacing=\"0\">\r\n"
						+ "    <tbody>\r\n" + "      <tr style=\"vertical-align: top\">\r\n"
						+ "        <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\r\n"
						+ "          <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\" style=\"background-color: #f9f9f9;\"><![endif]-->\r\n"
						+ "\r\n" + "\r\n" + "\r\n"
						+ "          <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\r\n"
						+ "            <div class=\"u-row\" style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">\r\n"
						+ "              <div style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\r\n"
						+ "                <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->\r\n"
						+ "\r\n"
						+ "                <!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->\r\n"
						+ "                <div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\r\n"
						+ "                  <div style=\"height: 100%;width: 100% !important;\">\r\n"
						+ "                    <!--[if (!mso)&(!IE)]><!-->\r\n"
						+ "                    <div style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\r\n"
						+ "                      <!--<![endif]-->\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:20px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\r\n"
						+ "                                <tr>\r\n"
						+ "                                  <td style=\"padding-right: 0px;padding-left: 0px;\" align=\"center\">\r\n"
						+ "                                    <a href=\"http://localhost:8080/\" target=\"_blank\">\r\n"
						+ "                                      <img align=\"center\" border=\"0\" src=\"https://assets.unlayer.com/projects/174425/1690386236297-logo%20(1).png\" alt=\"Image\" title=\"Image\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;width: 32%;max-width: 179.2px;\"\r\n"
						+ "                                        width=\"179.2\" />\r\n"
						+ "                                    </a>\r\n" + "                                  </td>\r\n"
						+ "                                </tr>\r\n" + "                              </table>\r\n"
						+ "\r\n" + "                            </td>\r\n" + "                          </tr>\r\n"
						+ "                        </tbody>\r\n" + "                      </table>\r\n" + "\r\n"
						+ "                      <!--[if (!mso)&(!IE)]><!-->\r\n" + "                    </div>\r\n"
						+ "                    <!--<![endif]-->\r\n" + "                  </div>\r\n"
						+ "                </div>\r\n" + "                <!--[if (mso)|(IE)]></td><![endif]-->\r\n"
						+ "                <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\r\n"
						+ "              </div>\r\n" + "            </div>\r\n" + "          </div>\r\n" + "\r\n"
						+ "\r\n" + "\r\n" + "\r\n" + "\r\n"
						+ "          <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\r\n"
						+ "            <div class=\"u-row\" style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #003399;\">\r\n"
						+ "              <div style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\r\n"
						+ "                <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #003399;\"><![endif]-->\r\n"
						+ "\r\n"
						+ "                <!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->\r\n"
						+ "                <div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\r\n"
						+ "                  <div style=\"height: 100%;width: 100% !important;\">\r\n"
						+ "                    <!--[if (!mso)&(!IE)]><!-->\r\n"
						+ "                    <div style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\r\n"
						+ "                      <!--<![endif]-->\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:40px 10px 10px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\r\n"
						+ "                                <tr>\r\n"
						+ "                                  <td style=\"padding-right: 0px;padding-left: 0px;\" align=\"center\">\r\n"
						+ "\r\n"
						+ "                                    <img align=\"center\" border=\"0\" src=\"https://cdn.templates.unlayer.com/assets/1597218650916-xxxxc.png\" alt=\"Image\" title=\"Image\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;width: 26%;max-width: 150.8px;\"\r\n"
						+ "                                      width=\"150.8\" />\r\n" + "\r\n"
						+ "                                  </td>\r\n" + "                                </tr>\r\n"
						+ "                              </table>\r\n" + "\r\n"
						+ "                            </td>\r\n" + "                          </tr>\r\n"
						+ "                        </tbody>\r\n" + "                      </table>\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <div style=\"font-size: 14px; color: #e5eaf5; line-height: 140%; text-align: center; word-wrap: break-word;\">\r\n"
						+ "                                <p style=\"font-size: 14px; line-height: 140%;\"><strong>T H A N K S&nbsp; &nbsp;F O R&nbsp; &nbsp;S I G N I N G&nbsp; &nbsp;U P !</strong></p>\r\n"
						+ "                              </div>\r\n" + "\r\n" + "                            </td>\r\n"
						+ "                          </tr>\r\n" + "                        </tbody>\r\n"
						+ "                      </table>\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:0px 10px 31px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <div style=\"font-size: 14px; color: #e5eaf5; line-height: 140%; text-align: center; word-wrap: break-word;\">\r\n"
						+ "                                <p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-size: 28px; line-height: 39.2px;\"><strong><span style=\"line-height: 39.2px; font-size: 28px;\">Verify Your E-mail Address </span></strong>\r\n"
						+ "                                  </span>\r\n" + "                                </p>\r\n"
						+ "                              </div>\r\n" + "\r\n" + "                            </td>\r\n"
						+ "                          </tr>\r\n" + "                        </tbody>\r\n"
						+ "                      </table>\r\n" + "\r\n"
						+ "                      <!--[if (!mso)&(!IE)]><!-->\r\n" + "                    </div>\r\n"
						+ "                    <!--<![endif]-->\r\n" + "                  </div>\r\n"
						+ "                </div>\r\n" + "                <!--[if (mso)|(IE)]></td><![endif]-->\r\n"
						+ "                <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\r\n"
						+ "              </div>\r\n" + "            </div>\r\n" + "          </div>\r\n" + "\r\n"
						+ "\r\n" + "\r\n" + "\r\n" + "\r\n"
						+ "          <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\r\n"
						+ "            <div class=\"u-row\" style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">\r\n"
						+ "              <div style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\r\n"
						+ "                <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->\r\n"
						+ "\r\n"
						+ "                <!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->\r\n"
						+ "                <div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\r\n"
						+ "                  <div style=\"height: 100%;width: 100% !important;\">\r\n"
						+ "                    <!--[if (!mso)&(!IE)]><!-->\r\n"
						+ "                    <div style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\r\n"
						+ "                      <!--<![endif]-->\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:33px 55px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <div style=\"font-size: 14px; line-height: 160%; text-align: center; word-wrap: break-word;\">\r\n"
						+ "                                <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 22px; line-height: 35.2px;\">Hi, </span></p>\r\n"
						+ "                                <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 18px; line-height: 28.8px;\">You're almost ready to get started. Please click on the button below to verify your email address and start shopping and grab Upto 75% Discounts! </span></p>\r\n"
						+ "                              </div>\r\n" + "\r\n" + "                            </td>\r\n"
						+ "                          </tr>\r\n" + "                        </tbody>\r\n"
						+ "                      </table>\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <!--[if mso]><style>.v-button {background: transparent !important;}</style><![endif]-->\r\n"
						+ "                              <div align=\"center\">\r\n"
						+ "                                <!--[if mso]><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"\" style=\"height:46px; v-text-anchor:middle; width:235px;\" arcsize=\"8.5%\"  stroke=\"f\" fillcolor=\"#ff6600\"><w:anchorlock/><center style=\"color:#FFFFFF;\"><![endif]-->\r\n"
						+ "                                <a href=" + verificationUrl
						+ " target=\"_blank\" class=\"v-button\" style=\"box-sizing: border-box;display: inline-block;text-decoration: none;-webkit-text-size-adjust: none;text-align: center;color: #FFFFFF; background-color: #ff6600; border-radius: 4px;-webkit-border-radius: 4px; -moz-border-radius: 4px; width:auto; max-width:100%; overflow-wrap: break-word; word-break: break-word; word-wrap:break-word; mso-border-alt: none;font-size: 14px;\">\r\n"
						+ "                                  <span style=\"display:block;padding:14px 44px 13px;line-height:120%;\"><span style=\"font-size: 16px; line-height: 19.2px;\"><strong><span style=\"line-height: 19.2px; font-size: 16px;\">VERIFY YOUR EMAIL</span></strong>\r\n"
						+ "                                  </span>\r\n"
						+ "                                  </span>\r\n" + "                                </a>"
						+ "                                <!--[if mso]></center></v:roundrect><![endif]-->\r\n"
						+ "                              </div>\r\n" + "\r\n" + "                            </td>\r\n"
						+ "                          </tr>\r\n" + "                        </tbody>\r\n"
						+ "                      </table>\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:33px 55px 60px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <div style=\"font-size: 14px; line-height: 160%; text-align: center; word-wrap: break-word;\">\r\n"
						+ "                                <p style=\"line-height: 160%; font-size: 14px;\"><span style=\"font-size: 18px; line-height: 28.8px;\">Thanks,</span></p>\r\n"
						+ "                                <p style=\"line-height: 160%; font-size: 14px;\"><span style=\"font-size: 18px; line-height: 28.8px;\">Eworld Team</span></p>\r\n"
						+ "                              </div>\r\n" + "\r\n" + "                            </td>\r\n"
						+ "                          </tr>\r\n" + "                        </tbody>\r\n"
						+ "                      </table>\r\n" + "\r\n"
						+ "                      <!--[if (!mso)&(!IE)]><!-->\r\n" + "                    </div>\r\n"
						+ "                    <!--<![endif]-->\r\n" + "                  </div>\r\n"
						+ "                </div>\r\n" + "                <!--[if (mso)|(IE)]></td><![endif]-->\r\n"
						+ "                <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\r\n"
						+ "              </div>\r\n" + "            </div>\r\n" + "          </div>\r\n" + "\r\n"
						+ "\r\n" + "\r\n" + "\r\n" + "\r\n"
						+ "          <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\r\n"
						+ "            <div class=\"u-row\" style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #e5eaf5;\">\r\n"
						+ "              <div style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\r\n"
						+ "                <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #e5eaf5;\"><![endif]-->\r\n"
						+ "\r\n"
						+ "                <!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->\r\n"
						+ "                <div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\r\n"
						+ "                  <div style=\"height: 100%;width: 100% !important;\">\r\n"
						+ "                    <!--[if (!mso)&(!IE)]><!-->\r\n"
						+ "                    <div style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\r\n"
						+ "                      <!--<![endif]-->\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:41px 55px 18px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <div style=\"font-size: 14px; color: #003399; line-height: 160%; text-align: center; word-wrap: break-word;\">\r\n"
						+ "                                <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 20px; line-height: 32px;\"><strong>Get in touch</strong></span></p>\r\n"
						+ "                                <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 16px; line-height: 25.6px; color: #000000;\">+91 919 333 4444</span></p>\r\n"
						+ "                                <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 16px; line-height: 25.6px; color: #000000;\">Eworldecom99@gmail.com</span></p>\r\n"
						+ "                              </div>\r\n" + "\r\n" + "                            </td>\r\n"
						+ "                          </tr>\r\n" + "                        </tbody>\r\n"
						+ "                      </table>\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px 10px 33px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n" + "                              <div align=\"center\">\r\n"
						+ "                                <div style=\"display: table; max-width:244px;\">\r\n"
						+ "                                  <!--[if (mso)|(IE)]><table width=\"244\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"border-collapse:collapse;\" align=\"center\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse; mso-table-lspace: 0pt;mso-table-rspace: 0pt; width:244px;\"><tr><![endif]-->\r\n"
						+ "\r\n" + "\r\n"
						+ "                                  <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 17px;\" valign=\"top\"><![endif]-->\r\n"
						+ "                                  <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"width: 32px !important;height: 32px !important;display: inline-block;border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 17px\">\r\n"
						+ "                                    <tbody>\r\n"
						+ "                                      <tr style=\"vertical-align: top\">\r\n"
						+ "                                        <td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\r\n"
						+ "                                          <a href=\"https://facebook.com/\" title=\"Facebook\" target=\"_blank\">\r\n"
						+ "                                            <img src=\"https://cdn.tools.unlayer.com/social/icons/circle-black/facebook.png\" alt=\"Facebook\" title=\"Facebook\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">\r\n"
						+ "                                          </a>\r\n"
						+ "                                        </td>\r\n"
						+ "                                      </tr>\r\n"
						+ "                                    </tbody>\r\n"
						+ "                                  </table>\r\n"
						+ "                                  <!--[if (mso)|(IE)]></td><![endif]-->\r\n" + "\r\n"
						+ "                                  <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 17px;\" valign=\"top\"><![endif]-->\r\n"
						+ "                                  <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"width: 32px !important;height: 32px !important;display: inline-block;border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 17px\">\r\n"
						+ "                                    <tbody>\r\n"
						+ "                                      <tr style=\"vertical-align: top\">\r\n"
						+ "                                        <td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\r\n"
						+ "                                          <a href=\"https://linkedin.com/\" title=\"LinkedIn\" target=\"_blank\">\r\n"
						+ "                                            <img src=\"https://cdn.tools.unlayer.com/social/icons/circle-black/linkedin.png\" alt=\"LinkedIn\" title=\"LinkedIn\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">\r\n"
						+ "                                          </a>\r\n"
						+ "                                        </td>\r\n"
						+ "                                      </tr>\r\n"
						+ "                                    </tbody>\r\n"
						+ "                                  </table>\r\n"
						+ "                                  <!--[if (mso)|(IE)]></td><![endif]-->\r\n" + "\r\n"
						+ "                                  <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 17px;\" valign=\"top\"><![endif]-->\r\n"
						+ "                                  <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"width: 32px !important;height: 32px !important;display: inline-block;border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 17px\">\r\n"
						+ "                                    <tbody>\r\n"
						+ "                                      <tr style=\"vertical-align: top\">\r\n"
						+ "                                        <td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\r\n"
						+ "                                          <a href=\"https://instagram.com/\" title=\"Instagram\" target=\"_blank\">\r\n"
						+ "                                            <img src=\"https://cdn.tools.unlayer.com/social/icons/circle-black/instagram.png\" alt=\"Instagram\" title=\"Instagram\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">\r\n"
						+ "                                          </a>\r\n"
						+ "                                        </td>\r\n"
						+ "                                      </tr>\r\n"
						+ "                                    </tbody>\r\n"
						+ "                                  </table>\r\n"
						+ "                                  <!--[if (mso)|(IE)]></td><![endif]-->\r\n" + "\r\n"
						+ "                                  <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 17px;\" valign=\"top\"><![endif]-->\r\n"
						+ "                                  <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"width: 32px !important;height: 32px !important;display: inline-block;border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 17px\">\r\n"
						+ "                                    <tbody>\r\n"
						+ "                                      <tr style=\"vertical-align: top\">\r\n"
						+ "                                        <td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\r\n"
						+ "                                          <a href=\"https://youtube.com/\" title=\"YouTube\" target=\"_blank\">\r\n"
						+ "                                            <img src=\"https://cdn.tools.unlayer.com/social/icons/circle-black/youtube.png\" alt=\"YouTube\" title=\"YouTube\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">\r\n"
						+ "                                          </a>\r\n"
						+ "                                        </td>\r\n"
						+ "                                      </tr>\r\n"
						+ "                                    </tbody>\r\n"
						+ "                                  </table>\r\n"
						+ "                                  <!--[if (mso)|(IE)]></td><![endif]-->\r\n" + "\r\n"
						+ "                                  <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 0px;\" valign=\"top\"><![endif]-->\r\n"
						+ "                                  <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"width: 32px !important;height: 32px !important;display: inline-block;border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 0px\">\r\n"
						+ "                                    <tbody>\r\n"
						+ "                                      <tr style=\"vertical-align: top\">\r\n"
						+ "                                        <td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\r\n"
						+ "                                          <a href=\"https://email.com/\" title=\"Email\" target=\"_blank\">\r\n"
						+ "                                            <img src=\"https://cdn.tools.unlayer.com/social/icons/circle-black/email.png\" alt=\"Email\" title=\"Email\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">\r\n"
						+ "                                          </a>\r\n"
						+ "                                        </td>\r\n"
						+ "                                      </tr>\r\n"
						+ "                                    </tbody>\r\n"
						+ "                                  </table>\r\n"
						+ "                                  <!--[if (mso)|(IE)]></td><![endif]-->\r\n" + "\r\n"
						+ "\r\n"
						+ "                                  <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\r\n"
						+ "                                </div>\r\n" + "                              </div>\r\n"
						+ "\r\n" + "                            </td>\r\n" + "                          </tr>\r\n"
						+ "                        </tbody>\r\n" + "                      </table>\r\n" + "\r\n"
						+ "                      <!--[if (!mso)&(!IE)]><!-->\r\n" + "                    </div>\r\n"
						+ "                    <!--<![endif]-->\r\n" + "                  </div>\r\n"
						+ "                </div>\r\n" + "                <!--[if (mso)|(IE)]></td><![endif]-->\r\n"
						+ "                <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\r\n"
						+ "              </div>\r\n" + "            </div>\r\n" + "          </div>\r\n" + "\r\n"
						+ "\r\n" + "\r\n" + "\r\n" + "\r\n"
						+ "          <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\r\n"
						+ "            <div class=\"u-row\" style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #003399;\">\r\n"
						+ "              <div style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\r\n"
						+ "                <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #003399;\"><![endif]-->\r\n"
						+ "\r\n"
						+ "                <!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->\r\n"
						+ "                <div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\r\n"
						+ "                  <div style=\"height: 100%;width: 100% !important;\">\r\n"
						+ "                    <!--[if (!mso)&(!IE)]><!-->\r\n"
						+ "                    <div style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\r\n"
						+ "                      <!--<![endif]-->\r\n" + "\r\n"
						+ "                      <table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">\r\n"
						+ "                        <tbody>\r\n" + "                          <tr>\r\n"
						+ "                            <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">\r\n"
						+ "\r\n"
						+ "                              <div style=\"font-size: 14px; color: #fafafa; line-height: 180%; text-align: center; word-wrap: break-word;\">\r\n"
						+ "                                <p style=\"font-size: 14px; line-height: 180%;\"><span style=\"font-size: 16px; line-height: 28.8px;\">Copyrights © Eworld All Rights Reserved</span></p>\r\n"
						+ "                              </div>\r\n" + "\r\n" + "                            </td>\r\n"
						+ "                          </tr>\r\n" + "                        </tbody>\r\n"
						+ "                      </table>\r\n" + "\r\n"
						+ "                      <!--[if (!mso)&(!IE)]><!-->\r\n" + "                    </div>\r\n"
						+ "                    <!--<![endif]-->\r\n" + "                  </div>\r\n"
						+ "                </div>\r\n" + "                <!--[if (mso)|(IE)]></td><![endif]-->\r\n"
						+ "                <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->\r\n"
						+ "              </div>\r\n" + "            </div>\r\n" + "          </div>\r\n" + "\r\n"
						+ "\r\n" + "\r\n" + "          <!--[if (mso)|(IE)]></td></tr></table><![endif]-->\r\n"
						+ "        </td>\r\n" + "      </tr>\r\n" + "    </tbody>\r\n" + "  </table>\r\n"
						+ "  <!--[if mso]></div><![endif]-->\r\n" + "  <!--[if IE]></div><![endif]-->\r\n"
						+ "</body>\r\n" + "\r\n" + "</html>";

				boolean sendEmail = this.emailService.sendEmail(subject, message, to);

				if (sendEmail) {
					user.setPassword(this.passwordEncoder.encode(user.getPassword()));
					user.setRole("ROLE_USER");
					user.setProfilePic("default_profile.png");
					user.setCreationDateTime(LocalDateTime.now());

					this.userService.saveUser(user);

					session.setAttribute("message",
							new Msg("We sent Confirmation on Your Email Address! Verify it", "alert-success"));
				} else {
					session.setAttribute("message", new Msg("Enter Valid Email!!", "alert-danger"));
					return "signup";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
			return "signup";
		}

		return "redirect:/login";

	}

	@GetMapping("/account-verification/verification={verificationId}")
	private String signupVerification(@PathVariable String verificationId, HttpSession session) {

		try {

			User user = this.userService.getUserByEmailVerification(verificationId);

			user.setStatus(true);
			this.userService.saveUser(user);

			session.setAttribute("message",
					new Msg("Your Account is Verified!! Please Login and grab it", "alert-success"));

			return "redirect:/login";

		} catch (Exception e) {
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));

			return "redirect:/signup";
		}

	}

}