package com.eworld.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// rating value is between 1 and 5
	private int value;

	private LocalDateTime ratedDate;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Rating() {
		super();
	}

	public Rating(Long id, int value, LocalDateTime ratedDate, Product product, User user) {
		super();
		this.id = id;
		this.value = value;
		this.ratedDate = ratedDate;
		this.product = product;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
