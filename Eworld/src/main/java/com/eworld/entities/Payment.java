package com.eworld.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String rzporderId;

	private int amount;

	private String receipt;

	private String status;

	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	private String paymentId;

	public Payment() {
		super();
	}

	public Payment(long id, String rzporderId, int amount, String receipt, String status, Order order,
			String paymentId) {
		super();
		this.id = id;
		this.rzporderId = rzporderId;
		this.amount = amount;
		this.receipt = receipt;
		this.status = status;
		this.order = order;
		this.paymentId = paymentId;
	}

	public Payment(String rzporderId, int amount, String receipt, String status, Order order, String paymentId) {
		super();
		this.rzporderId = rzporderId;
		this.amount = amount;
		this.receipt = receipt;
		this.status = status;
		this.order = order;
		this.paymentId = paymentId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRzporderId() {
		return rzporderId;
	}

	public void setRzporderId(String rzporderId) {
		this.rzporderId = rzporderId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

}
