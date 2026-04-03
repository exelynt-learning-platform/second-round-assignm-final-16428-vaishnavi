package com.exelyent.task.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.exelyent.task.entity.User;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final String role;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;



    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().name();
        this.enabled = user.isEnabled();
        this.authorities = java.util.Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }



	public static CustomUserDetails from(User user) {
        return new CustomUserDetails(user);
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
        return username;
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
        return enabled;
    }

	public Long getId() {
		return id;
	}



	public String getEmail() {
		return email;
	}



	public String getRole() {
		return role;
	}
    
    
}