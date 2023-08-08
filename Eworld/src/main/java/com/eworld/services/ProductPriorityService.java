package com.eworld.services;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductPriorityRepository;
import com.eworld.entities.Product;
import com.eworld.entities.ProductPriority;

@Service
public class ProductPriorityService {

	@Autowired
	private ProductPriorityRepository productPriorityRepository;

	public Map<String, Product> getHighPriorityProducts() {
		return this.productPriorityRepository.findAll().stream()
				.collect(Collectors.toMap(ProductPriority::getBannerImage, ProductPriority::getProduct));
	}

}
