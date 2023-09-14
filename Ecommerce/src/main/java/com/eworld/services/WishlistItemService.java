package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.WishlistItemRepository;
import com.eworld.entities.Product;
import com.eworld.entities.User;
import com.eworld.entities.WishlistItem;

@Service
public class WishlistItemService {

	@Autowired
	private WishlistItemRepository wishlistItemRepository;

	public WishlistItem getWishlistById(String id) {
		return this.wishlistItemRepository.findById(id).get();
	}

	public List<WishlistItem> getWishlistByUser(User user) {
		return this.wishlistItemRepository.findByUser(user);
	}

	public void addProductToWishlist(WishlistItem wishlistItem) {
		this.wishlistItemRepository.save(wishlistItem);
	}

	public void removeProductFromWishlist(WishlistItem wishlistItem) {
		this.wishlistItemRepository.delete(wishlistItem);
	}

	public boolean existsByUserAndProduct(User user, Product product) {
		return this.wishlistItemRepository.existsByUserAndProduct(user, product);
	}

}
