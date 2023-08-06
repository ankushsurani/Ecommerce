package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductRepository;
import com.eworld.entities.Product;

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
	
	public List<Product> getHighPriorityProducts(){
		return this.productRepository.findByHighPriorityIsTrue();
	}

}
