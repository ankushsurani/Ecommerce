package com.eworld.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	public User findByEmail(String email);

	public User findByEmailVerification(String emailVerification);

	public List<User> findAllUserByStatusFalse();

	boolean existsAddressesByIdAndAddressActiveTrue(String id);

}