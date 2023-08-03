package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.OrderRepository;
import com.eworld.entities.Order;
import com.eworld.entities.User;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public void saveOrder(Order order) {
		this.orderRepository.save(order);
	}

	public Order findById(int orderId) {
		return this.orderRepository.findById(orderId).get();
	}

	public List<Order> getAllOrder() {
		return this.orderRepository.findAll();
	}

	public List<Order> getOrderByStatus(String status) {
		return this.orderRepository.getOrderByStatus(status);
	}

	public List<Order> findOrderByStatus(String status1, String status2, String status3) {
		return this.orderRepository.findOrderByStatus(status1, status2, status3);
	}

	public List<Order> getOrderByUser(User user) {
		return this.orderRepository.findAllByUser(user);
	}

}
