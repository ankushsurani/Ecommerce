package com.eworld.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	SimpleUrlAuthenticationSuccessHandler userSuccessHandler = new SimpleUrlAuthenticationSuccessHandler("/");
	SimpleUrlAuthenticationSuccessHandler adminSuccessHandler = new SimpleUrlAuthenticationSuccessHandler(
			"/admin/dashboard");

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (final GrantedAuthority grantedAuthority : authorities) {
			String authorityName = grantedAuthority.getAuthority();
			if (authorityName.equals("ROLE_ADMIN")) {
				// if the user is an ADMIN delegate to the adminSuccessHandler
				this.adminSuccessHandler.onAuthenticationSuccess(request, response, authentication);
				return;
			}
		}
		// if the user is not an admin delegate to the userSuccessHandler
		this.userSuccessHandler.onAuthenticationSuccess(request, response, authentication);
	}
}
