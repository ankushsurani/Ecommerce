package com.eworld.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

	public List<User> getAllUser() {
		return this.userRepository.findAll();
	}

	public User getUserById(String userId) {
		return this.userRepository.findByUserId(userId);
	}

	public User getUserByEmailVerification(String emailVerification) {
		return this.userRepository.findByEmailVerification(emailVerification);
	}

	public static boolean has24HoursCompleted(LocalDateTime startDate, LocalDateTime endDate) {
		Duration duration = Duration.between(startDate, endDate);
		long hours = duration.toHours();
		return hours >= 1;
	}

	// This method will run every 15 minutes (900000 milliseconds)
	@Scheduled(fixedRate = 60000)
	public void autoRunFunction() {
		List<User> users = this.userRepository.findAllUserByStatusFalse();

		for (User user : users) {
			LocalDateTime signupTime = user.getCreationDateTime();

			LocalDateTime currentTime = LocalDateTime.now();

			if (has24HoursCompleted(signupTime, currentTime)) {
				this.userRepository.delete(user);
			}
		}
	}

}