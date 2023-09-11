package com.eworld.controllers.admin;

import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
			model.addAttribute("user", user);
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

			Slice<Product> latestProducts = this.productService
					.getFilteredAndSortedProducts(new FilterRequest("Latest"), pageable);

			AdminAnalytics analytics = new AdminAnalytics(countTotalSaleOfLastYear, countTotalSaleOfLastMonth,
					ordersOfLastYear, ordersOfLastMonth, userJoinedLastYear, userJoinedLastMonth);

			model.addAttribute("appName", this.appName).addAttribute("analytics", analytics)
					.addAttribute("topSellingProducts", topSellingProducts)
					.addAttribute("latestProducts", latestProducts);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "admin/admin_dashboard";
	}

	@GetMapping("/manage-products/page={pageNumber}")
	public String manageProducts(@PathVariable("pageNumber") int pageNumber, Model model) {
		Slice<Product> products = this.productService.getFilteredAndSortedProducts(new FilterRequest(),
				PageRequest.of(pageNumber, this.pageSize));
		boolean hasNextPage = products.hasNext();

		model.addAttribute("products", products).addAttribute("hasNextPage", hasNextPage);

		return "admin/manage_products";
	}

	@GetMapping("/add-product")
	public String addProduct(Model model) {

		List<Category> categories = this.categoryService.getAllCategories();

		model.addAttribute("product", new Product()).addAttribute("categories", categories).addAttribute("editProduct",
				false);
		return "admin/add-product";
	}

	@PostMapping("/add-product-process")
	public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
			@RequestParam("catId") String catId, @RequestParam("product_images") List<MultipartFile> files,
			HttpSession session, Model model) {

		model.addAttribute("title", "Add product - Eworld");

		try {

			if (result.hasErrors()) {
				model.addAttribute("product", product);
				List<Category> categories = this.categoryService.getAllCategories();
				model.addAttribute("categories", categories);
				return "admin/add-product";
			}

			Category category = this.categoryService.getCategory(catId);

			product.setCategory(category);
			product.setAddedDate(LocalDateTime.now());
			product.setActive(true);

			synchronized (this.productService) {
		        this.productService.saveProduct(product);
		    }

			try {

				if (!product.getId().isEmpty()) {
					List<ProductImage> oldImages = this.productImageService.findByProduct(product);
					List<String> oldProductPicNames = oldImages.stream().map(oldImage -> oldImage.getProductPic())
							.collect(Collectors.toList());
					FileImageUpload.deleteImages(oldProductPicNames, path);
					this.productImageService.deleteProductImages(oldImages);
				}

				for (MultipartFile file : files) {
					String fileName = FileImageUpload.uploadImage(path, file);
					ProductImage productImage = new ProductImage();
					productImage.setProductPic(fileName);
					productImage.setProduct(product);
					this.productImageService.saveImage(productImage);
				}
				session.setAttribute("message", new Msg(product.getName() + " added successfully", "alert-success"));

			} catch (Exception e) {
				e.printStackTrace();
				session.setAttribute("message", new Msg(e.getMessage(), "alert-danger"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/admin/add-product";

	}

	@GetMapping("/edit-product/{productId}")
	public String editProduct(@PathVariable("productId") String productId, Model model) {

		Product product = this.productService.getProduct(productId);
		List<Category> categories = this.categoryService.getAllCategories();

		model.addAttribute("product", product).addAttribute("categories", categories).addAttribute("editProduct", true);
		return "admin/add-product";
	}

	@PostMapping("/delete-product/page={pageNumber}/{productId}")
	public String deleteProduct(@PathVariable("pageNumber") int pageNumber, @PathVariable("productId") String productId,
			HttpSession session) {

		try {

			Product product = this.productService.getProduct(productId);
			this.productService.removeProductFromView(product);
			session.setAttribute("message", new Msg(product.getName() + " is deleted successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg(e.getMessage(), "alert-danger"));
		}

		return "redirect:/admin/manage-products/page=" + pageNumber;
	}

	@GetMapping("/manage-categories/page={pageNumber}")
	public String manageCategories(@PathVariable("pageNumber") int pageNumber, Model model) {
		Pageable pageable = PageRequest.of(pageNumber, 10);
		Slice<Category> categories = this.categoryService.findAllByPage(pageable);
		boolean hasNextPage = categories.hasNext();

		model.addAttribute("categories", categories).addAttribute("hasNextPage", hasNextPage);

		return "admin/manage_categories";
	}

	@GetMapping("/add-category")
	public String addCategory(Model model) {

		model.addAttribute("category", new Category()).addAttribute("editCategory", false);
		return "admin/add-category";
	}

	@PostMapping("/add-category-process")
	public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
			@RequestParam("category_image") MultipartFile file, HttpSession session, Model model) {

		model.addAttribute("title", "Add category - Eworld");

		try {

			if (result.hasErrors()) {
				model.addAttribute("category", category);
				return "admin/add-category";
			}

			String fileName = FileImageUpload.uploadImage(path, file);
			category.setCategoryImage(fileName);
			category.setActive(true);

			this.categoryService.saveCategory(category);
			session.setAttribute("message", new Msg("Category added successfully", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/admin/add-category";

	}

	@GetMapping("/edit-category/{categoryId}")
	public String editCategory(@PathVariable("categoryId") String categoryId, Model model) {

		Category category = this.categoryService.getCategory(categoryId);

		model.addAttribute("category", category).addAttribute("editCategory", true);
		return "admin/add-category";
	}

	@PostMapping("/delete-category/page={pageNumber}/{categoryId}")
	public String deleteCategory(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("categoryId") String categoryId, HttpSession session) {

		try {

			Category category = this.categoryService.getCategory(categoryId);
			this.categoryService.removeCategoryFromView(category);
			session.setAttribute("message", new Msg(category.getTitle() + " is deleted successfully", "alert-success"));

		} catch (Exception e) {
			session.setAttribute("message", new Msg(e.getMessage(), "alert-danger"));
		}

		return "redirect:/admin/manage-categories/page=" + pageNumber;
	}

	@GetMapping("/current-orders")
	public String currentOrders(Model model,
			@RequestParam(name = "statusSelect", required = false) DeliveryStatus status, HttpSession session) {
		model.addAttribute("title", "Orders - Eworld");

		try {

			if (status == null || status.equals(DeliveryStatus.ALL)) {
				List<Order> orders = this.orderService.getOrdersByStatuses(List.of(DeliveryStatus.AWAITINGPAYMENT,
						DeliveryStatus.AWAITINGPICKUP, DeliveryStatus.PARTIALLYSHIPPED));
				model.addAttribute("orders", orders);
			} else {
				List<Order> statusOrders = this.orderService.getOrderByStatus(status);
				model.addAttribute("orders", statusOrders);
			}

			model.addAttribute("allDeliveryStatuses", DeliveryStatus.values());

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "admin/current_orders";
	}

	// change order status
	@PostMapping("/change-status/{orderId}")
	public String changeOrderStatus(@PathVariable("orderId") String orderId,
			@RequestParam("status") DeliveryStatus status,
			@RequestParam(name = "deliveryDate", required = false) String deliveryDate, Model model,
			HttpSession session) throws ParseException {
		model.addAttribute("title", "Change Status - Eworld");

		try {

			Order order = this.orderService.findById(orderId);

			if (status.equals(DeliveryStatus.COMPLETED)) {
				order.setDeliveryStatus(status);
				order.setDeliveryDate(LocalDateTime.now());
			} else {
				order.setDeliveryStatus(status);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate localDate = LocalDate.parse(deliveryDate, formatter);
				LocalDateTime dateTime = localDate.atStartOfDay();
				order.setDeliveryDate(dateTime);
			}

			this.orderService.saveOrder(order);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Msg("Something Went Wrong!!", "alert-danger"));
		}

		return "redirect:/admin/current-orders";

	}

}
