package com.eworld.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int pId;

	@NotBlank
	@Size(min = 3, max = 100)
	private String pName;

	@NotBlank
	@Size(min = 1, max = 100)
	private String pBrandName;

	@NotBlank
	@Size(min = 10, max = 65535)
	@Column(length = 65535)
	private String pDescription;

	@Min(value = 10, message = "Price must be greater than 10")
	@Max(value = 500000, message = "Price must be less than 500000")
	private int pPrice;

	@Max(value = 70, message = "Discount must be less than 70%")
	private int pDiscount;

	@Min(value = 1, message = "Quantity must be greater than or equal to than 1")
	@Max(value = 10000, message = "Quantity must be less than or equal to 10000")
	private int pQuantity;

	private LocalDateTime addedDate;

	@OneToMany(mappedBy = "product")
	private List<ProductImage> productImages = new ArrayList<>();

	@ManyToOne
	@JsonIgnore
	private Category category;

	public Product() {
		super();
	}

	public Product(String pName, String pBrandName, String pDescription, int pPrice, int pDiscount, int pQuantity,
			LocalDateTime addedDate, List<ProductImage> productImages, Category category) {
		super();
		this.pName = pName;
		this.pBrandName = pBrandName;
		this.pDescription = pDescription;
		this.pPrice = pPrice;
		this.pDiscount = pDiscount;
		this.pQuantity = pQuantity;
		this.addedDate = addedDate;
		this.productImages = productImages;
		this.category = category;
	}

	public Product(int pId, String pName, String pBrandName, String pDescription, int pPrice, int pDiscount,
			LocalDateTime addedDate, int pQuantity, List<ProductImage> productImages, Category category) {
		super();
		this.pId = pId;
		this.pName = pName;
		this.pBrandName = pBrandName;
		this.pDescription = pDescription;
		this.pPrice = pPrice;
		this.pDiscount = pDiscount;
		this.pQuantity = pQuantity;
		this.addedDate = addedDate;
		this.productImages = productImages;
		this.category = category;
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getpDescription() {
		return pDescription;
	}

	public void setpDescription(String pDescription) {
		this.pDescription = pDescription;
	}

	public int getpPrice() {
		return pPrice;
	}

	public void setpPrice(int pPrice) {
		this.pPrice = pPrice;
	}

	public int getpDiscount() {
		return pDiscount;
	}

	public void setpDiscount(int pDiscount) {
		this.pDiscount = pDiscount;
	}

	public int getpQuantity() {
		return pQuantity;
	}

	public void setpQuantity(int pQuantity) {
		this.pQuantity = pQuantity;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<ProductImage> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<ProductImage> productImages) {
		this.productImages = productImages;
	}

	public String getpBrandName() {
		return pBrandName;
	}

	public void setpBrandName(String pBrandName) {
		this.pBrandName = pBrandName;
	}

	public LocalDateTime getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(LocalDateTime addedDate) {
		this.addedDate = addedDate;
	}

	// calculate price after discount
	public int getPriceAfterApplyingDiscount() {
		int discount = (int) ((this.getpDiscount() / 100.0) * this.getpPrice());
		return this.getpPrice() - discount;
	}

}
