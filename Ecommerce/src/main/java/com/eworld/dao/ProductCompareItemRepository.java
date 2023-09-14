package com.eworld.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Product;
import com.eworld.entities.ProductCompareItem;
import com.eworld.entities.User;

@Repository
public interface ProductCompareItemRepository extends JpaRepository<ProductCompareItem, String> {

	public List<ProductCompareItem> findAllByUser(User user);

	boolean existsByUserAndProduct(User user, Product product);

}
