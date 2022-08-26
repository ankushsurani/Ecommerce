package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.UserRepository;
import com.eworld.entities.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public void saveUser(User user) {
		this.userRepository.save(user);
	}
	
	public User findByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
	
	public List<User> getAllUser(){
		return this.userRepository.findAll();
	}
	
	public User getUserById(int userId) {
		return this.userRepository.findById(userId).get();
	}
	
}
