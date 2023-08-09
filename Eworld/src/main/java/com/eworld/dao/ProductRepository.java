package com.eworld.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	public List<Product> findByCategory_id(int category_id);

	// search
	public List<Product> findBypNameContaining(String productName);

	@Query("SELECT p FROM Product p ORDER BY p.addedDate DESC")
	Page<Product> find10RecentProducts(Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.pId IN (SELECT r.product.pId FROM Rating r WHERE r.ratedDate >= :cutoffDate GROUP BY r.product.pId ORDER BY COUNT(r.product.pId) DESC, MAX(r.ratedDate) DESC)")
	List<Product> findMostRatedProductsOfRecentDates(@Param("cutoffDate") LocalDateTime cutoffDate);

}
