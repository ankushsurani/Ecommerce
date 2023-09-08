package com.eworld.controllers.admin;

import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eworld.dto.AdminAnalytics;
import com.eworld.dto.FilterRequest;
import com.eworld.entities.Category;
import com.eworld.entities.Order;
import com.eworld.entities.Product;
import com.eworld.entities.ProductImage;
import com.eworld.entities.User;
import com.eworld.enumstype.DeliveryStatus;
import com.eworld.helper.FileImageUpload;
import com.eworld.helper.Msg;
import com.eworld.services.CategoryService;
import com.eworld.services.OrderService;
import com.eworld.services.ProductImageService;
import com.eworld.services.ProductService;
import com.eworld.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

	@Value("${product.image}")
	private String path;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductImageService productImageService;

	@Autowired
	private OrderService orderService;

	@Value("${spring.application.name}")
	private String appName;
	
	private int pageSize = 10;

	@ModelAttribute
	public void currentUser(Principal principal, Model model) {
		if (principal != null) {
			User user = this.userService.findByEmail(principal.getName());
			model.addAttribute("currentUser", user);
		}
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model, HttpSession session) {
		model.addAttribute("title", "Dashboard - Eworld");

		try {

			long countTotalSaleOfLastYear = this.orderService
					.countTotalSaleOfLastYear(LocalDateTime.now().minusYears(1));
			long countTotalSaleOfLastMonth = this.orderService
					.countTotalSaleOfLastYear(LocalDateTime.now().minusMonths(1));

			long ordersOfLastYear = this.orderService.countOrdersLastYear(LocalDateTime.now().minusYears(1));
			long ordersOfLastMonth = this.orderService.countOrdersLastYear(LocalDateTime.now().minusMonths(1));

			long userJoinedLastYear = this.userService.countUserJoinInLastYear(LocalDateTime.now().minusYears(1));
			long userJoinedLastMonth = this.userService.countUserJoinInLastYear(LocalDateTime.now().minusMonths(1));
			
			Pageable pageable = PageRequest.of(0, this.pageSize);
			Slice<Product> topSellingProducts = this.productService
					.getFilteredAndSortedProducts(new FilterRequest("Popularity"), pageable);
			Slice<Product> topRatedProducts = this.productService
					.getFilteredAndSortedProducts(new FilterRequest("Rating"), pageable);

			AdminAnalytics analytics = new AdminAnalytics(countTotalSaleOfLastYear, countTotalSaleOfLastMonth, ordersOfLastYear, ordersOfLastMonth,
					userJoinedLastYear, userJoinedLastMonth);

			model.addAttribute("appName", this.appName)
			.addAttribute("analytics", analytics)
			.addAttribute("topSellingProducts", topSellingProducts)
			.addAttribute("topRatedProducts", topRatedProducts);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "admin/admin_dashboard";
	}
	
	@GetMapping("/manage-products/page={pageNumber}")
	public String manageProducts(@PathVariable("pageNumber") int pageNumber, Model model) {
		Slice<Product> products = this.productService.getFilteredAndSortedProducts(new FilterRequest(), PageRequest.of(pageNumber, this.pageSize));
		boolean hasNextPage = products.hasNext();
		
		model.addAttribute("products", products)
		.addAttribute("hasNextPage", hasNextPage);
		
		return "admin/manage_products";
	}

	@PostMapping("/add-category")
	public String addCategory(@ModelAttribute("category") Category category, HttpSession session, Model model) {

		model.addAttribute("title", "Add category - Eworld");

		try {

			this.categoryService.saveCategory(category);
			session.setAttribute("message", new Msg("Category added successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/admin/dashboard";

	}

	@PostMapping("/add-product")
	public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
			@RequestParam("catId") String catId, @RequestParam("product_images") List<MultipartFile> files,
			HttpSession session, Model model) {

		model.addAttribute("title", "Add product - Eworld");

		try {

			if (result.hasErrors()) {
				model.addAttribute("product", product);

				List<User> users = this.userService.getAllUser();
				model.addAttribute("users", users);

//				List<Product> products = this.productService.getAllProducts(0,12);
//				model.addAttribute("products", products);

				List<Category> categories = this.categoryService.getAllCategories();
				model.addAttribute("categories", categories);

				session.setAttribute("message", new Msg("Please Fill valid details of Product", "alert-warning"));
				return "admin/dashboard";
			}

			Category category = this.categoryService.getCategory(catId);

			product.setCategory(category);

			this.productService.saveProduct(product);

			try {

				for (MultipartFile file : files) {
					String fileName = FileImageUpload.uploadImage(path, file);
					ProductImage productImage = new ProductImage();
					productImage.setProductPic(fileName);
					productImage.setProduct(product);
					this.productImageService.saveImage(productImage);
				}

			} catch (Exception e) {
				e.printStackTrace();
				session.setAttribute("message", new Msg(e.getMessage(), "alert-danger"));
				return "redirect:/admin/dashboard";
			}

			session.setAttribute("message", new Msg("Product added successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/admin/dashboard";

	}

	@GetMapping("/current-orders")
	public String currentOrders(Model model, @RequestParam(name = "statusSelect", required = false) String status,
			HttpSession session) {
		model.addAttribute("title", "Orders - Eworld");

		try {

			if (status == null || status.equals("all")) {
				List<Order> orders = this.orderService.getOrdersByStatuses(List.of(DeliveryStatus.AWAITINGPAYMENT,
						DeliveryStatus.AWAITINGPICKUP, DeliveryStatus.PARTIALLYSHIPPED));
				model.addAttribute("orders", orders);
			} else {
				List<Order> statusOrders = this.orderService.getOrderByStatus(status);
				model.addAttribute("orders", statusOrders);
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "admin/current_orders";
	}

	// change order status
	@PostMapping("/change-status/{orderId}")
	public String changeOrderStatus(@PathVariable("orderId") String orderId, @RequestParam("status") String status,
			@RequestParam(name = "deliveryDate", required = false) String deliveryDate, Model model,
			HttpSession session) throws ParseException {
		model.addAttribute("title", "Change Status - Eworld");

		try {

			Order order = this.orderService.findById(orderId);

			if (status.equals("Completed")) {
//				order.setStatus(status); 
				order.setDeliveryDate(LocalDateTime.now());
			} else {
//				order.setStatus(status);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDateTime date = (LocalDateTime) formatter.parse(deliveryDate);
				order.setDeliveryDate(date);

			}

			this.orderService.saveOrder(order);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/admin/current-orders";

	}

}
