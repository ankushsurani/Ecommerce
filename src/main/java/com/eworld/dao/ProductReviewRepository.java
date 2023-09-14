package com.eworld.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eworld.entities.ProductReview;
import com.eworld.entities.Rating;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {

	@Query("SELECT pr FROM ProductReview as pr WHERE pr.rating = :rating")
	public Optional<ProductReview> getRatingByUserAndProduct(Rating rating);

	@Query("SELECT pr FROM ProductReview pr WHERE pr.rating.id IN ?1")
	public List<ProductReview> findAllByRatingIds(List<String> ratingIds);

}
