package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.CartRepository;
import com.eworld.entities.Cart;
import com.eworld.entities.Product;
import com.eworld.entities.User;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	public void saveCart(Cart cart) {
		this.cartRepository.save(cart);
	}

	public Cart findByProduct(Product product) {
		return this.cartRepository.findByProduct(product);
	}

	public List<Cart> findByUser(User user) {
		return this.cartRepository.findByUser(user);
	}

	public Cart getCart(String cartId) {
		return this.cartRepository.findById(cartId).get();
	}

	public void removeCart(String cartId) {
		this.cartRepository.deleteById(cartId);
	}

}
