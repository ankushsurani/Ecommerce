package com.eworld.dao;

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
	public List<Product> findByNameContaining(String name);

	@Query("SELECT p FROM Product p ORDER BY p.addedDate DESC")
	Page<Product> findLatestProducts(Pageable pageable);

	Page<Product> findAllByOrderByDiscountDesc(Pageable pageable);
	
	Page<Product> findAllByOrderByAvgRatingDesc(Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId)"
            + " AND (:minPrice IS NULL OR p.price >= :minPrice)"
            + " AND (:maxPrice IS NULL OR p.price <= :maxPrice)"
            + " AND (:brandName IS NULL OR p.brandName = :brandName)"
            + " ORDER BY "
            + " CASE WHEN :sortBy = 'popularity' THEN (SELECT COUNT(o) FROM Order o WHERE o.product = p AND o.deliveryStatus = 'SUCCESSFULLY_DELIVERED') END DESC, "
            + " CASE WHEN :sortBy = 'rating' THEN p.avgRating END DESC, "
            + " CASE WHEN :sortBy = 'latest' THEN p.addedDate END DESC, "
            + " CASE WHEN :sortBy = 'highDiscount' THEN p.discount END DESC, "
            + " CASE WHEN :sortBy = 'priceHighToLow' THEN p.price END DESC, "
            + " CASE WHEN :sortBy = 'priceLowToHigh' THEN p.price END ASC"
    )
    Page<Product> filterAndSortProducts(@Param("categoryId") Integer categoryId,
                                        @Param("minPrice") Integer minPrice,
                                        @Param("maxPrice") Integer maxPrice,
                                        @Param("brandName") String brandName,
                                        @Param("sortBy") String sortBy,
                                        Pageable pageable);

}
