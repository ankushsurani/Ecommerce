package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductReviewRepository;
import com.eworld.entities.ProductReview;
import com.eworld.entities.Rating;

@Service
public class ProductReviewService {

	@Autowired
	private ProductReviewRepository productReviewRepository;

	public ProductReview getProductReviewByRating(Rating rating) {
		return this.productReviewRepository.getRatingByUserAndProduct(rating).orElse(new ProductReview());
	}
	
	public void saveProductReview(ProductReview review) {
		this.productReviewRepository.save(review);
	}
	
	public List<ProductReview> getProductReviewsByIds(List<String> ratingIds) {
		return this.productReviewRepository.findAllByRatingIds(ratingIds);
	}

}
