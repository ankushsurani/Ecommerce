package com.eworld.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Product;
import com.eworld.entities.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {

	public List<ProductImage> findByProduct(Product product);

}
