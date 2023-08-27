package com.eworld.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.RatingRepository;
import com.eworld.entities.Product;
import com.eworld.entities.Rating;
import com.eworld.entities.User;

@Service
public class RatingService {

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private OrderService orderService;

	public boolean addRating(User user, Product product, Rating rating) {
		boolean hasUserOrderedProduct = false;
		if (this.orderService.hasUserOrderedProduct(user, product)) {
			rating.setProduct(product);
			rating.setUser(user);
			rating.setRatedDate(LocalDateTime.now());
			this.ratingRepository.save(rating);
			
			hasUserOrderedProduct = true;
		}
		return hasUserOrderedProduct;
	}

	public double getAvgRating(Product product) {
		List<Rating> ratings = getRatingsByProduct(product);

		double sumOfRatings = ratings.stream().mapToInt(Rating::getValue).sum();
		Double avgRating = sumOfRatings / ratings.size();

		return avgRating;
	}

	public Rating getRatingByUserAndProduct(Product product, User user) {
		return this.ratingRepository.findByProductAndUser(product, user).orElse(null);
	}

	public List<Rating> getRatingsByProduct(Product product) {
		return this.ratingRepository.findByProduct(product);
	}
	
	public List<String> getRatingsIdsByProduct(Product product) {
		return this.ratingRepository.findAllRatingIdsByProduct(product);
	}

}
