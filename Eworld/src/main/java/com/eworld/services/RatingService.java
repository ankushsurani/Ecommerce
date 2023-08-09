package com.eworld.services;

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

	public void rateProduct(User user, Product product, int value) {
		Rating rating = new Rating();
		rating.setUser(user);
		rating.setProduct(product);
		rating.setValue(value);

		this.ratingRepository.save(rating);
	}

}
