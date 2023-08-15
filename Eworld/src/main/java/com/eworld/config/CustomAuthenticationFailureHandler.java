package com.eworld.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		// Custom logic to handle authentication failure and send messages
		String errorMessage = "Invalid username or password.";

		// You can add additional messages based on the specific error condition
		if (exception instanceof DisabledException) {
			errorMessage = "Your account is unverified. Please Verify on sended Email By Eworld";
		}
		response.sendRedirect("/login?error=" + errorMessage);
	}

}
