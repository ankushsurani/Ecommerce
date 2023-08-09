package com.eworld.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eworld.dao.OrderRepository;
import com.eworld.entities.DeliveryStatus;
import com.eworld.entities.Order;
import com.eworld.entities.Product;
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

	public List<Order> getOrdersByStatuses(List<DeliveryStatus> statuses) {
		return orderRepository.findOrdersByStatuses(statuses);
	}

	public List<Order> getOrderByUser(User user) {
		return this.orderRepository.findAllByUser(user);
	}

	public Page<Product> getTopSellingProductsInLast15Days(int page, int size) {
		LocalDateTime cutoffDate = LocalDateTime.now().minus(15, ChronoUnit.DAYS);
		PageRequest pageRequest = PageRequest.of(page, size);
		return orderRepository.findTopSellingProductsInLast15Days(cutoffDate, pageRequest);
	}

	public boolean hasUserOrderedProduct(User user, Product product) {
		return this.orderRepository.existsByUserAndProductAndDeliveryStatus(user, product,
				DeliveryStatus.SUCCESS);
	}

}
