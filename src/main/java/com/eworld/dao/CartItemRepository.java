package com.eworld.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.CartItem;
import com.eworld.entities.Product;
import com.eworld.entities.User;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

	public CartItem findByProduct(Product product);

	public List<CartItem> findByUser(User user);

	public CartItem findByUserAndProduct(User user, Product product);

	boolean existsByUserAndProduct(User user, Product product);

}
