package com.eworld.helper;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.eworld.entities.User;

public class CustomUUIDGenerator implements IdentifierGenerator {

	private static final String USER_PREFIX = "SELLER_";
	private static final String ALL_PREFIX = "ID_";

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		String prefix = "";

		if (object instanceof User) {
			prefix = USER_PREFIX;
		}
		
		prefix = ALL_PREFIX;

		return prefix + java.util.UUID.randomUUID().toString();
	}
}