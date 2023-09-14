package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.CartItemRepository;
import com.eworld.entities.CartItem;
import com.eworld.entities.Product;
import com.eworld.entities.User;

@Service
public class CartService {

	@Autowired
	private CartItemRepository cartItemRepository;

	public void saveCartItem(CartItem cartItem) {
		this.cartItemRepository.save(cartItem);
	}

	public List<CartItem> getCartItemsByIds(List<String> cartItemIds) {
		return this.cartItemRepository.findAllById(cartItemIds);
	}

	public CartItem findByProduct(Product product) {
		return this.cartItemRepository.findByProduct(product);
	}

	public List<CartItem> findByUser(User user) {
		return this.cartItemRepository.findByUser(user);
	}

	public CartItem getCartItem(String cartItemId) {
		return this.cartItemRepository.findById(cartItemId).get();
	}

	public void removeCartItem(String cartId) {
		this.cartItemRepository.deleteById(cartId);
	}

	public boolean existsByUserAndProduct(User user, Product product) {
		return this.cartItemRepository.existsByUserAndProduct(user, product);
	}

	public CartItem getByUserAndProduct(User user, Product product) {
		return this.cartItemRepository.findByUserAndProduct(user, product);
	}

}
