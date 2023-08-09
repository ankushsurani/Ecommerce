package com.eworld.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eworld.entities.DeliveryStatus;
import com.eworld.entities.Order;
import com.eworld.entities.Product;
import com.eworld.entities.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Query("select o from Order o where o.deliveryStatus =:s")
	public List<Order> getOrderByStatus(@Param("s") String status);

	@Query("SELECT o FROM Order o WHERE o.deliveryStatus IN (:statuses)")
    List<Order> findOrdersByStatuses(@Param("statuses") List<DeliveryStatus> statuses);
	
	public List<Order> findAllByUser(User user);

	@Query("SELECT o.product FROM Order o WHERE o.createdDate >= :cutoffDate GROUP BY o.product ORDER BY COUNT(o.product) DESC")
	Page<Product> findTopSellingProductsInLast15Days(LocalDateTime cutoffDate, Pageable pageable);

	boolean existsByUserAndProductAndDeliveryStatus(User user, Product product, DeliveryStatus deliveryStatus);

}
