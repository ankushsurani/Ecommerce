package com.eworld.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String title;

	private String description;
	
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private List<Product> products = new ArrayList<>();

	public Category() {
		super();
	}

	public Category(int id, String title, String description, List<Product> products) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.products = products;
	}

	public Category(String title, String description, List<Product> products) {
		super();
		this.title = title;
		this.description = description;
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
	
	
	
}
