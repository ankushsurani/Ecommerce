package com.eworld.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eworld.dto.AccountOrderDto;
import com.eworld.entities.Address;
import com.eworld.entities.Order;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.enumstype.DeliveryStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

	@Query("select o from Order o where o.deliveryStatus =:s")
	public List<Order> getOrderByStatus(@Param("s") String status);

	@Query("SELECT o FROM Order o WHERE o.deliveryStatus IN (:statuses)")
	List<Order> findOrdersByStatuses(@Param("statuses") List<DeliveryStatus> statuses);

	@Query("SELECT o FROM Order o WHERE o.user.id =:userId AND o.deliveryStatus IN :statuses")
	List<Order> findOrdersByUserAndStatuses(@Param("userId") String userId,
			@Param("statuses") List<DeliveryStatus> statuses);

	@Query("SELECT new com.eworld.dto.AccountOrderDto(o.product, o.orderPrice, o.quantity, o.deliveryStatus) FROM Order o WHERE o.user.email = :email and o.deliveryStatus != :deliveryStatus ORDER BY o.createdDate DESC")
	List<AccountOrderDto> findProductAndQuantitiesByEmail(@Param("email") String email, @Param("deliveryStatus") DeliveryStatus deliveryStatus);

	List<Order> findByDeliveryStatusAndCreatedDateBefore(DeliveryStatus deliveryStatus, LocalDateTime dateTime);
	
	@Query("SELECT p FROM Product p JOIN Order o ON p = o.product GROUP BY p ORDER BY SUM(o.quantity) DESC")
	Page<Product> findTopSellingProducts(Pageable pageable);

	boolean existsByUserAndProductAndDeliveryStatus(User user, Product product, DeliveryStatus deliveryStatus);

	boolean existsByAddressAndUser(Address address, User user);
	
	@Query("SELECT SUM(o.orderPrice) FROM Order o WHERE o.createdDate >= :time")
    public long countTotalSaleOfLastTime(LocalDateTime time);
	
	@Query("SELECT COUNT(o) FROM Order o WHERE o.createdDate >= :time")
	public Long countOrdersByLastTime(LocalDateTime time);

}
