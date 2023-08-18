package com.eworld.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public Order findById(String orderId) {
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

	public Map<Product, Integer> getOrderByUser(String email) {
		List<Object[]> orders = this.orderRepository.findProductAndQuantitiesByEmail(email);
		
		return orders.stream().collect(Collectors.toMap(obj -> (Product) obj[0], obj -> (Integer) obj[1]));
	}

	public List<Product> getPopularProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return orderRepository.findTopSellingProducts(pageable).toList();
	}

	public boolean hasUserOrderedProduct(User user, Product product) {
		return this.orderRepository.existsByUserAndProductAndDeliveryStatus(user, product, DeliveryStatus.SUCCESS);
	}

}
