package com.eworld.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Cart;
import com.eworld.entities.Product;
import com.eworld.entities.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

	public Cart findByProduct(Product product);

	public List<Cart> findByUser(User user);

}
