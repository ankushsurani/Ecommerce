package com.eworld.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ProductImage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String productPic;
	
	@ManyToOne
	@JsonIgnore
	private Product product;

	public ProductImage() {
		super();
	}

	public ProductImage(int id, String productPic, Product product) {
		super();
		this.id = id;
		this.productPic = productPic;
		this.product = product;
	}

	public ProductImage(String productPic, Product product) {
		super();
		this.productPic = productPic;
		this.product = product;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductPic() {
		return productPic;
	}

	public void setProductPic(String productPic) {
		this.productPic = productPic;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	
	
}
