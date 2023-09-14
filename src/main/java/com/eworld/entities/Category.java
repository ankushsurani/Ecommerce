package com.eworld.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Category {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

	@NotBlank
	@Size(min = 3, max = 25, message = "title length must have 3 to 25 characters")
	private String title;

	@NotBlank
	@Size(min = 3, max = 25, message = "cachy title length must have 3 to 25 characters")
	private String cachyTitle;

	@NotBlank
	@Size(min = 3, max = 500, message = "description length must have 3 to 500 characters")
	private String description;

	private String categoryImage;

	private boolean active;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private List<Product> products = new ArrayList<>();

	public Category() {
		super();
	}

	public Category(String id, String title, String cachyTitle, String description, String categoryImage,
			boolean active, List<Product> products) {
		super();
		this.id = id;
		this.title = title;
		this.cachyTitle = cachyTitle;
		this.description = description;
		this.categoryImage = categoryImage;
		this.active = active;
		this.products = products;
	}

	public Category(String title, String cachyTitle, String description, String categoryImage, boolean active,
			List<Product> products) {
		super();
		this.title = title;
		this.cachyTitle = cachyTitle;
		this.description = description;
		this.categoryImage = categoryImage;
		this.active = active;
		this.products = products;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getCategoryImage() {
		return categoryImage;
	}

	public void setCategoryImage(String categoryImage) {
		this.categoryImage = categoryImage;
	}

	public String getCachyTitle() {
		return cachyTitle;
	}

	public void setCachyTitle(String cachyTitle) {
		this.cachyTitle = cachyTitle;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
