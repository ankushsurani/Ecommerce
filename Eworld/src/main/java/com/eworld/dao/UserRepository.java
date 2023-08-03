package com.eworld.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	public User findByEmail(String email);

	public User findByUserId(String userId);

	public User findByEmailVerification(String emailVerification);

	public List<User> findAllUserByStatusFalse();

}