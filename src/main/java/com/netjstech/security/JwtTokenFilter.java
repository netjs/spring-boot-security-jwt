package com.netjstech.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.netjstech.service.UserDetailsServiceImpl;

public class JwtTokenFilter extends OncePerRequestFilter{
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String token = getTokenFromRequest(request);
			System.out.println("Token-- " + token);
			if (token != null && jwtTokenUtil.validateJwtToken(token)) {
				String username = jwtTokenUtil.getUserNameFromJwtToken(token);
				//System.out.println("User Name--JwtTokenFilter-- " + username);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				//System.out.println("Authorities--JwtTokenFilter-- " + userDetails.getAuthorities());
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			//logger.error("Cannot set user authentication: {}", e);
			throw new RuntimeException("Cannot set user authentication" + e.getMessage());
		}
		filterChain.doFilter(request, response);
	}
	
	private String getTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		System.out.println("Token from Request-- " + token);
		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			// remove "Bearer "
			return token.substring(7, token.length());
		}
		return null;
	}
}
