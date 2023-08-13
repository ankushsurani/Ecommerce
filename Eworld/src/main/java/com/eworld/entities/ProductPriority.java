package com.eworld.entities;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class ProductPriority {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

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

	public ProductPriority(String id, boolean highPriority, String bannerImage, Product product) {
		super();
		this.id = id;
		this.highPriority = highPriority;
		this.bannerImage = bannerImage;
		this.product = product;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
