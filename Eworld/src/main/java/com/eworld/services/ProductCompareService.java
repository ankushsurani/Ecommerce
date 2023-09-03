package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductCompareItemRepository;
import com.eworld.entities.Product;
import com.eworld.entities.ProductCompareItem;
import com.eworld.entities.User;

@Service
public class ProductCompareService {

	@Autowired
	private ProductCompareItemRepository productCompareRepository;

	public void addProductIntoComparison(ProductCompareItem productCompareItem) {
		this.productCompareRepository.save(productCompareItem);
	}

	public ProductCompareItem getProductFromComparisonById(String productCompareId) {
		return this.productCompareRepository.findById(productCompareId).get();
	}
	
	public List<ProductCompareItem> getAllProductFromComparisonByUser(User user) {
		return this.productCompareRepository.findAllByUser(user);
	}
	
	public void removeProductFromComparison(String productCompareItemId) {
		this.productCompareRepository.deleteById(productCompareItemId);
	}
	
	public boolean existsByUserAndProduct(User user, Product product) {
		return this.productCompareRepository.existsByUserAndProduct(user, product);
	}

}
