package com.eworld.entities;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Rating {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

	private int value;

	private LocalDateTime ratedDate;

	@ManyToOne
	@JsonIgnore
	private Product product;

	@OneToOne(mappedBy = "rating", cascade = CascadeType.ALL)
	private ProductReview productReview;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Rating() {
		super();
	}

	public Rating(String id, int value, LocalDateTime ratedDate, Product product, ProductReview productReview,
			User user) {
		super();
		this.id = id;
		this.value = value;
		this.ratedDate = ratedDate;
		this.product = product;
		this.productReview = productReview;
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public ProductReview getProductReview() {
		return productReview;
	}

	public void setProductReview(ProductReview productReview) {
		this.productReview = productReview;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getRatedDate() {
		return ratedDate;
	}

	public void setRatedDate(LocalDateTime ratedDate) {
		this.ratedDate = ratedDate;
	}

}
