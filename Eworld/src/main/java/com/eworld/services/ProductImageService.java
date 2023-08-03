package com.eworld.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductImageRepository;
import com.eworld.entities.ProductImage;

@Service
public class ProductImageService {

	@Autowired
	private ProductImageRepository productImageRepository;

	public void saveImage(ProductImage productImages) {
		this.productImageRepository.save(productImages);
	}

}
