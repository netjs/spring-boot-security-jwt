package com.netjstech.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CustomUserBean implements UserDetails {
	private static final long serialVersionUID = -4709084843450077569L;	
	private Integer id; 
	private String userName; 
	private String email;
	@JsonIgnore
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	CustomUserBean(Integer id, String userName, String email, 
			String password, Collection<? extends GrantedAuthority> authorities){
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}
	
	public static CustomUserBean createInstance(User user) {
		List<GrantedAuthority> authorities = user.getRoles()
												 .stream()
												 .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
												 .collect(Collectors.toList());
		return new CustomUserBean(user.getId(), user.getUserName(), 
				user.getEmail(), user.getPassword(), authorities);
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	public Integer getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object rhs) {
		if (rhs instanceof CustomUserBean) {
			return userName.equals(((CustomUserBean) rhs).userName);
		}
		return false;
	}

	/**
	 * Returns the hashcode of the {@code username}.
	 */
	@Override
	public int hashCode() {
		return userName.hashCode();
	}
}
