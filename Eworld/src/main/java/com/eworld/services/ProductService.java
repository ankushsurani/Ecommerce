package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductRepository;
import com.eworld.dto.FilterRequest;
import com.eworld.entities.Product;
import com.eworld.entities.Rating;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public void saveProduct(Product product) {
		this.productRepository.save(product);
	}

	public Product getProduct(String id) {
		return this.productRepository.findById(id).get();
	}

	public List<String> getAllBrandName(String categoryId) {
		return this.productRepository.getAllBrandName(categoryId);
	}

	public List<Product> getBypNameContaining(String productName) {
		return this.productRepository.findByNameContaining(productName);
	}

	public Slice<Product> getFilteredAndSortedProducts(FilterRequest filterRequest, Pageable pageable) {
		return productRepository.filterAndSortProducts(filterRequest.getCategoryId(), filterRequest.getMinPrice(),
				filterRequest.getMaxPrice(), filterRequest.getBrandName(), filterRequest.getSortType(), pageable);
	}

	public boolean rateProduct(String productId, Rating rating) {
		boolean flag = false;

		try {

			Product product = this.productRepository.findById(productId).get();
			List<Rating> ratings = product.getRatings();
			ratings.add(rating);

			double sumOfRatings = ratings.stream().mapToInt(Rating::getValue).sum();
			Double avgRating = sumOfRatings / ratings.size();
			product.setAvgRating(avgRating);

			this.productRepository.save(product);

			flag = true;

		} catch (Exception e) {
			System.out.println("Something Went Wrong");
		}

		return flag;
	}

}
