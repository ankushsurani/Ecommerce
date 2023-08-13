package com.eworld.helper;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.eworld.entities.Address;
import com.eworld.entities.Cart;
import com.eworld.entities.Category;
import com.eworld.entities.CategoryPriority;
import com.eworld.entities.Order;
import com.eworld.entities.Payment;
import com.eworld.entities.Product;
import com.eworld.entities.ProductImage;
import com.eworld.entities.ProductPriority;
import com.eworld.entities.Rating;
import com.eworld.entities.User;

public class CustomUUIDGenerator implements IdentifierGenerator {

	private static final String USER_PREFIX = "USER_";
	private static final String ADDRESS_PREFIX = "ADDRESS_";
	private static final String CART_PREFIX = "CART_";
	private static final String CATEGORY_PREFIX = "CATEGORY_";
	private static final String CATEGORYPRIO_PREFIX = "CATEGORYPRIO_";
	private static final String ORDER_PREFIX = "ORDER_";
	private static final String PAYMENT_PREFIX = "PAYMENT_";
	private static final String PRODUCT_PREFIX = "PRODUCT_";
	private static final String PRODUCTIMG_PREFIX = "PRODUCTIMG_";
	private static final String PRODUCTPRIO_PREFIX = "PRODUCTPRIO_";
	private static final String RATING_PREFIX = "RATING_";

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		String prefix = "";

		if (object instanceof User) {
			prefix = USER_PREFIX;
		} else if (object instanceof Address) {
			prefix = ADDRESS_PREFIX;
		} else if (object instanceof Cart) {
			prefix = CART_PREFIX;
		} else if (object instanceof Category) {
			prefix = CATEGORY_PREFIX;
		} else if (object instanceof CategoryPriority) {
			prefix = CATEGORYPRIO_PREFIX;
		} else if (object instanceof Order) {
			prefix = ORDER_PREFIX;
		} else if (object instanceof Payment) {
			prefix = PAYMENT_PREFIX;
		} else if (object instanceof Product) {
			prefix = PRODUCT_PREFIX;
		} else if (object instanceof ProductImage) {
			prefix = PRODUCTIMG_PREFIX;
		} else if (object instanceof ProductPriority) {
			prefix = PRODUCTPRIO_PREFIX;
		} else if (object instanceof Rating) {
			prefix = RATING_PREFIX;
		}

		return prefix + java.util.UUID.randomUUID().toString();
	}
}