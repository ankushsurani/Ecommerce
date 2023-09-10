package com.eworld.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Product {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

	@NotBlank
	@Size(min = 3, max = 100)
	private String name;

	@NotBlank
	@Size(min = 1, max = 100)
	private String brandName;

	@NotBlank
	@Size(min = 10, max = 65535)
	@Column(length = 65535)
	private String description;

	@NotBlank
	@Size(min = 10, max = 65535)
	@Column(length = 65535, columnDefinition = "TEXT")
	private String additionalInformation;

	@Min(value = 10, message = "Price must be greater than 10")
	@Max(value = 500000, message = "Price must be less than 500000")
	private int price;

	@Max(value = 70, message = "Discount must be less than 70%")
	private int discount;

	@Min(value = 1, message = "Quantity must be greater than or equal to than 1")
	@Max(value = 10000, message = "Quantity must be less than or equal to 10000")
	private int quantity;

	private LocalDateTime addedDate;

	@Min(value = 1, message = "Delivery must be provided into minimum 1 day")
	@Max(value = 7, message = "Delivery must be provided into maximum 7 day")
	private int estimatedDeliveryDays;

	@NotBlank
	@Size(min = 10, max = 65535)
	@Column(length = 65535, columnDefinition = "TEXT")
	private String warranty;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<ProductImage> productImages = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<Rating> ratings = new ArrayList<>();

	private double avgRating = 0.0;

	private boolean active;

	@ManyToOne
	@JsonIgnore
	private Category category;

	public Product() {
		super();
	}

	public Product(String id, String name, String brandName, String description, String additionalInformation,
			int price, int discount, int quantity, LocalDateTime addedDate, int estimatedDeliveryDays, String warranty,
			List<ProductImage> productImages, List<Rating> ratings, double avgRating, boolean active,
			Category category) {
		super();
		this.id = id;
		this.name = name;
		this.brandName = brandName;
		this.description = description;
		this.additionalInformation = additionalInformation;
		this.price = price;
		this.discount = discount;
		this.quantity = quantity;
		this.addedDate = addedDate;
		this.estimatedDeliveryDays = estimatedDeliveryDays;
		this.warranty = warranty;
		this.productImages = productImages;
		this.ratings = ratings;
		this.avgRating = avgRating;
		this.active = active;
		this.category = category;
	}

	public Product(String name, String brandName, String description, String additionalInformation, int price,
			int discount, int quantity, LocalDateTime addedDate, int estimatedDeliveryDays, String warranty,
			List<ProductImage> productImages, List<Rating> ratings, double avgRating, boolean active,
			Category category) {
		super();
		this.name = name;
		this.brandName = brandName;
		this.description = description;
		this.additionalInformation = additionalInformation;
		this.price = price;
		this.discount = discount;
		this.quantity = quantity;
		this.addedDate = addedDate;
		this.estimatedDeliveryDays = estimatedDeliveryDays;
		this.warranty = warranty;
		this.productImages = productImages;
		this.ratings = ratings;
		this.avgRating = avgRating;
		this.active = active;
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public LocalDateTime getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(LocalDateTime addedDate) {
		this.addedDate = addedDate;
	}

	public List<ProductImage> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<ProductImage> productImages) {
		this.productImages = productImages;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public double getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(double avgRating) {
		this.avgRating = avgRating;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public int getEstimatedDeliveryDays() {
		return estimatedDeliveryDays;
	}

	public void setEstimatedDeliveryDays(int estimatedDeliveryDays) {
		this.estimatedDeliveryDays = estimatedDeliveryDays;
	}

	public String getWarranty() {
		return warranty;
	}

	public void setWarranty(String warranty) {
		this.warranty = warranty;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	// calculate price after discount
	public int getPriceAfterApplyingDiscount() {
		int discount = (int) ((this.getDiscount() / 100.0) * this.getPrice());
		return this.getPrice() - discount;
	}

}
