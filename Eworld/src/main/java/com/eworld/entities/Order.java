package com.eworld.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.eworld.enumstype.DeliveryStatus;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int order_id;

	private LocalDateTime createdDate;

	private LocalDateTime deliveryDate;

	@Enumerated(EnumType.STRING)
	private DeliveryStatus deliveryStatus;

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

	public Order(int order_id, LocalDateTime createdDate, LocalDateTime deliveryDate, DeliveryStatus deliveryStatus,
			String paymentType, int totalPayment, Address address, Product product, int quantity, User user) {
		super();
		this.order_id = order_id;
		this.createdDate = createdDate;
		this.deliveryDate = deliveryDate;
		this.deliveryStatus = deliveryStatus;
		this.paymentType = paymentType;
		this.totalPayment = totalPayment;
		this.address = address;
		this.product = product;
		this.quantity = quantity;
		this.user = user;
	}

	public Order(LocalDateTime createdDate, LocalDateTime deliveryDate, DeliveryStatus deliveryStatus,
			String paymentType, int totalPayment, Address address, Product product, int quantity, User user) {
		super();
		this.createdDate = createdDate;
		this.deliveryDate = deliveryDate;
		this.deliveryStatus = deliveryStatus;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
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
