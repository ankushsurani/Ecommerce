package com.eworld.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

import com.eworld.enumstype.DeliveryStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

	private LocalDateTime createdDate;

	private LocalDateTime deliveryDate;

	@Enumerated(EnumType.STRING)
	private DeliveryStatus deliveryStatus;

	private String paymentType;

	private int finalPrice;

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

	public Order(String id, LocalDateTime createdDate, LocalDateTime deliveryDate, DeliveryStatus deliveryStatus,
			String paymentType, int finalPrice, Address address, Product product, int quantity, User user) {
		super();
		this.id = id;
		this.createdDate = createdDate;
		this.deliveryDate = deliveryDate;
		this.deliveryStatus = deliveryStatus;
		this.paymentType = paymentType;
		this.finalPrice = finalPrice;
		this.address = address;
		this.product = product;
		this.quantity = quantity;
		this.user = user;
	}

	public Order(LocalDateTime createdDate, LocalDateTime deliveryDate, DeliveryStatus deliveryStatus,
			String paymentType, int finalPrice, Address address, Product product, int quantity, User user) {
		super();
		this.createdDate = createdDate;
		this.deliveryDate = deliveryDate;
		this.deliveryStatus = deliveryStatus;
		this.paymentType = paymentType;
		this.finalPrice = finalPrice;
		this.address = address;
		this.product = product;
		this.quantity = quantity;
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(int finalPrice) {
		this.finalPrice = finalPrice;
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
