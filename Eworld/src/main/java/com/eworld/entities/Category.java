package com.eworld.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String title;
	
	private String cachyTitle;

	private String description;
	
	private String categoryImage;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private List<Product> products = new ArrayList<>();

	public Category() {
		super();
	}

	public Category(int id, String title, String cachyTitle, String description, String categoryImage, List<Product> products) {
		super();
		this.id = id;
		this.title = title;
		this.cachyTitle = cachyTitle;
		this.description = description;
		this.categoryImage = categoryImage;
		this.products = products;
	}

	public Category(String title, String cachyTitle, String description, String categoryImage, List<Product> products) {
		super();
		this.title = title;
		this.cachyTitle = cachyTitle;
		this.description = description;
		this.categoryImage = categoryImage;
		this.products = products;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

}
