package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.ProductImageRepository;
import com.eworld.entities.Product;
import com.eworld.entities.ProductImage;

@Service
public class ProductImageService {

	@Autowired
	private ProductImageRepository productImageRepository;

	public void saveImage(ProductImage productImages) {
		this.productImageRepository.save(productImages);
	}

	public List<ProductImage> findByProduct(Product product) {
		return this.productImageRepository.findByProduct(product);
	}

	public void deleteProductImages(List<ProductImage> productImages) {
		this.productImageRepository.deleteAll(productImages);
	}

}
