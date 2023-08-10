package com.eworld.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductRepository;
import com.eworld.entities.Product;
import com.eworld.entities.Rating;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public void saveProduct(Product product) {
		this.productRepository.save(product);
	}

	public List<Product> getAllProducts() {
		return this.productRepository.findAll().stream().map(product -> {
			product.setAvgRating(getAverageRatingForProduct(product.getId()));
			return product;
		}).collect(Collectors.toList());
	}

	public List<Product> getProductByCategory(int category_id) {
		return this.productRepository.findByCategory_id(category_id).stream().map(product -> {
			product.setAvgRating(getAverageRatingForProduct(product.getId()));
			return product;
		}).collect(Collectors.toList());
	}

	public Product getProduct(int id) {
		Product product = this.productRepository.findById(id).get();
		product.setAvgRating(getAverageRatingForProduct(id));
		return product;
	}

	public List<Product> getBypNameContaining(String productName) {
		return this.productRepository.findByNameContaining(productName).stream().map(product -> {
			product.setAvgRating(getAverageRatingForProduct(product.getId()));
			return product;
		}).collect(Collectors.toList());
	}

	public List<Product> get10RecentProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return this.productRepository.find10RecentProducts(pageable).toList().stream().map(product -> {
			product.setAvgRating(getAverageRatingForProduct(product.getId()));
			return product;
		}).collect(Collectors.toList());
	}

	public List<Product> getMostRatedProductsOfRecentDates() {
		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
		return productRepository.findMostRatedProductsOfRecentDates(cutoffDate).stream().map(product -> {
			product.setAvgRating(getAverageRatingForProduct(product.getId()));
			return product;
		}).collect(Collectors.toList());
	}

	public Double getAverageRatingForProduct(int productId) {
		Product product = this.productRepository.findById(productId).get();

		List<Rating> ratings = product.getRatings();
		if (ratings.isEmpty()) {
			return 0.0;
		}

		double sumOfRatings = ratings.stream().mapToInt(Rating::getValue).sum();
		return sumOfRatings / ratings.size();
	}

	public List<Product> getProductsByHighDiscount(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAllByOrderByDiscountDesc(pageable).toList().stream().map(product -> {
			product.setAvgRating(getAverageRatingForProduct(product.getId()));
			return product;
		}).collect(Collectors.toList());
	}

}
