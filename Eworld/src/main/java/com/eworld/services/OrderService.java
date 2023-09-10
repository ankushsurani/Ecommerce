package com.eworld.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eworld.dao.OrderRepository;
import com.eworld.dto.AccountOrderDto;
import com.eworld.entities.Address;
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

	public List<Order> getOrderByStatus(DeliveryStatus status) {
		return this.orderRepository.getOrderByStatus(status);
	}

	public List<Order> getOrdersByStatuses(List<DeliveryStatus> statuses) {
		return orderRepository.findOrdersByStatuses(statuses);
	}

	public List<AccountOrderDto> getOrderByUserEmail(String email, DeliveryStatus status) {
		return this.orderRepository.findProductAndQuantitiesByEmail(email, status);
	}

	public List<Product> getPopularProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return orderRepository.findTopSellingProducts(pageable).toList();
	}

	public boolean hasUserOrderedProduct(User user, Product product) {
		return this.orderRepository.existsByUserAndProductAndDeliveryStatus(user, product, DeliveryStatus.COMPLETED);
	}

	public boolean anyOrderWithThisAddress(Address address, User user) {
		return this.orderRepository.existsByAddressAndUser(address, user);
	}

	public List<Order> findOrdersByUserAndStatuses(String userId, List<DeliveryStatus> deliveryStatus) {
		return this.orderRepository.findOrdersByUserAndStatuses(userId, deliveryStatus);
	}

	public long countTotalSaleOfLastYear(LocalDateTime dateTime) {
		return this.orderRepository.countTotalSaleOfLastTime(dateTime);
	}

	public long countOrdersLastYear(LocalDateTime dateTime) {
		return this.orderRepository.countOrdersByLastTime(dateTime);
	}

	// This method will run every 2 minutes (120000 milliseconds)
	@Scheduled(fixedRate = 120000)
	public void deleteOrdersWithAwaitingPayment() {
		List<Order> awaitingPaymentOrders = this.orderRepository.findByDeliveryStatusAndCreatedDateBefore(
				DeliveryStatus.AWAITINGPAYMENT, LocalDateTime.now().minusHours(1));

		this.orderRepository.deleteAll(awaitingPaymentOrders);
	}

}
