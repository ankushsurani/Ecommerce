package com.eworld.services;

import java.time.LocalDateTime;
import java.util.List;

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
		return this.productRepository.findAll();
	}

	public List<Product> getProductByCategory(int category_id) {
		return this.productRepository.findByCategory_id(category_id);
	}

	public Product getProduct(int pId) {
		return this.productRepository.findById(pId).get();
	}

	public List<Product> getBypNameContaining(String productName) {
		return this.productRepository.findBypNameContaining(productName);
	}

	public Page<Product> get10RecentProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return this.productRepository.find10RecentProducts(pageable);
	}

	public List<Product> getMostRatedProductsOfRecentDates() {
		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
		return productRepository.findMostRatedProductsOfRecentDates(cutoffDate);
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

}
