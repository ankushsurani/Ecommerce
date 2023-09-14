package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductRepository;
import com.eworld.dto.FilterRequest;
import com.eworld.entities.Product;

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
		return this.productRepository.findByNameContainingAndActiveIsTrue(productName);
	}

	public Slice<Product> getFilteredAndSortedProducts(FilterRequest filterRequest, Pageable pageable) {
		return productRepository.filterAndSortProducts(filterRequest.getCategoryId(), filterRequest.getMinPrice(),
				filterRequest.getMaxPrice(), filterRequest.getSearch(), filterRequest.getBrandName(),
				filterRequest.getSortType(), pageable);
	}

	public Page<Product> getSimilarProductsByCatId(String categoryId, Pageable pageable) {
		return this.productRepository.findByCategoryId(categoryId, pageable);
	}

	public void removeProductFromView(Product product) {
		product.setActive(false);
		this.productRepository.save(product);
	}

}
