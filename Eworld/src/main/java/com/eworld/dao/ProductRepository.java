package com.eworld.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

	// search
	public List<Product> findByNameContainingAndActiveIsTrue(String name);

	@Query("select DISTINCT p.brandName from Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId) AND p.brandName IS NOT NULL AND p.active=true")
	List<String> getAllBrandName(String categoryId);

	@Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId) AND p.active=true"
			+ " AND (:minPrice IS NULL OR p.price >= :minPrice)" + " AND (:maxPrice IS NULL OR p.price <= :maxPrice)"
			+ " AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', REPLACE(:search, ' ', '%'), '%')))"
			+ " AND (:brandName IS NULL OR p.brandName = :brandName)" + " ORDER BY "
			+ " CASE WHEN :sortType = 'Popularity' THEN (SELECT COUNT(o) FROM Order o WHERE o.product = p AND o.deliveryStatus = 'SUCCESSFULLY_DELIVERED') END DESC, "
			+ " CASE WHEN :sortType = 'Rating' THEN p.avgRating END DESC, "
			+ " CASE WHEN :sortType = 'Latest' THEN p.addedDate END DESC, "
			+ " CASE WHEN :sortType = 'HighDiscount' THEN p.discount END DESC, "
			+ " CASE WHEN :sortType = 'Price:HighToLow' THEN p.price END DESC, "
			+ " CASE WHEN :sortType = 'Price:LowToHigh' THEN p.price END ASC")
	Slice<Product> filterAndSortProducts(@Param("categoryId") String categoryId, @Param("minPrice") Integer minPrice,
			@Param("maxPrice") Integer maxPrice, @Param("search") String search, @Param("brandName") String brandName,
			@Param("sortType") String sortType, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.category.id=:categoryId AND p.active=true")
	Page<Product> findByCategoryId(String categoryId, Pageable pageable);

}
