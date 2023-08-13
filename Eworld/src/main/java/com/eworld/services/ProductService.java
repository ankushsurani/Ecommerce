package com.eworld.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductRepository;
import com.eworld.entities.Product;
import com.eworld.entities.Rating;
import com.eworld.helper.FilterRequest;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public void saveProduct(Product product) {
		this.productRepository.save(product);
	}

	public Page<Product> getAllProducts(Pageable pageable) {
		return this.productRepository.findAll(pageable);
	}

	public List<Product> getProductByCategory(int category_id) {
		return this.productRepository.findByCategory_id(category_id);
	}

	public Product getProduct(int id) {
		return this.productRepository.findById(id).get();
	}

	public List<Product> getBypNameContaining(String productName) {
		return this.productRepository.findByNameContaining(productName);
	}

	public List<Product> getLatestProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return this.productRepository.findLatestProducts(pageable).toList();
	}

	public List<Product> getMostRatedProducts(Pageable pageable) {
		return this.productRepository.findAllByOrderByAvgRatingDesc(pageable).toList();
	}

	public List<Product> getProductsByHighDiscount(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAllByOrderByDiscountDesc(pageable).toList();
	}

	public Page<Product> getFilteredAndSortedProducts(FilterRequest filterRequest, Pageable pageable) {
		return productRepository.filterAndSortProducts(filterRequest.getCategoryId(),
				filterRequest.getMinPrice(), filterRequest.getMaxPrice(), filterRequest.getBrandName(),
				filterRequest.getSortBy(), pageable);
	}
	
	public boolean rateProduct(int productId, Rating rating) {
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
