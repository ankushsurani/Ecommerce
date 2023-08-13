package com.eworld.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {

}
