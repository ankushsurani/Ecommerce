package com.eworld.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Product;
import com.eworld.entities.Rating;
import com.eworld.entities.User;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {

	@Query("SELECT r FROM Rating as r WHERE r.product = :product AND r.user = :user")
	public Optional<Rating> findByProductAndUser(Product product, User user);

	public List<Rating> findByProduct(Product product);
	
	@Query("SELECT r.id FROM Rating as r WHERE r.product = :product")
	public List<String> findAllRatingIdsByProduct(Product product);

}
