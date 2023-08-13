package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eworld.dao.OrderRepository;
import com.eworld.entities.Order;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.enumstype.DeliveryStatus;

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

	public List<Order> getOrdersByStatuses(List<DeliveryStatus> statuses) {
		return orderRepository.findOrdersByStatuses(statuses);
	}

	public List<Order> getOrderByUser(User user) {
		return this.orderRepository.findAllByUser(user);
	}

	public List<Product> getPopularProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return orderRepository.findTopSellingProducts(pageable).toList();
	}

	public boolean hasUserOrderedProduct(User user, Product product) {
		return this.orderRepository.existsByUserAndProductAndDeliveryStatus(user, product, DeliveryStatus.SUCCESS);
	}

}
