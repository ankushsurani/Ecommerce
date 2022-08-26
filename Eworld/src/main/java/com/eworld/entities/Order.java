package com.eworld.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int order_id;
	
	private Date createdDate;
	
	private Date deliveryDate;
	
	private String status;
	
	private String paymentType;
	
	private int totalPayment;
	
	@ManyToOne
	@JoinColumn(name = "address_id")
	private Address address;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	private int quantity;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Order() {
		super();
	}

	public Order(int order_id, Date createdDate, Date deliveryDate, String status, String paymentType, int totalPayment,
			Address address, Product product, int quantity, User user) {
		super();
		this.order_id = order_id;
		this.createdDate = createdDate;
		this.deliveryDate = deliveryDate;
		this.status = status;
		this.paymentType = paymentType;
		this.totalPayment = totalPayment;
		this.address = address;
		this.product = product;
		this.quantity = quantity;
		this.user = user;
	}

	public Order(Date createdDate, Date deliveryDate, String status, String paymentType, int totalPayment,
			Address address, Product product, int quantity, User user) {
		super();
		this.createdDate = createdDate;
		this.deliveryDate = deliveryDate;
		this.status = status;
		this.paymentType = paymentType;
		this.totalPayment = totalPayment;
		this.address = address;
		this.product = product;
		this.quantity = quantity;
		this.user = user;
	}

	public int getOrder_id() {
		return order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(int totalPayment) {
		this.totalPayment = totalPayment;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
	
}
