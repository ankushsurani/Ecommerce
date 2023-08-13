package com.eworld.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class ProductPriority {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int productPrioId;

	@Column(name = "is_high_priority")
	private boolean highPriority;

	@NotBlank
	private String bannerImage;

	@OneToOne
	@JoinColumn(name = "pId")
	private Product product;

	public ProductPriority() {
		super();
	}

	public ProductPriority(int productPrioId, boolean highPriority, String bannerImage, Product product) {
		super();
		this.productPrioId = productPrioId;
		this.highPriority = highPriority;
		this.bannerImage = bannerImage;
		this.product = product;
	}

	public int getProductPrioId() {
		return productPrioId;
	}

	public void setProductPrioId(int productPrioId) {
		this.productPrioId = productPrioId;
	}

	public boolean isHighPriority() {
		return highPriority;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

}
