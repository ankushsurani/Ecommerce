package com.eworld.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.entities.WishlistItem;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, String> {

	public List<WishlistItem> findByUser(User user);

	boolean existsByUserAndProduct(User user, Product product);

}
