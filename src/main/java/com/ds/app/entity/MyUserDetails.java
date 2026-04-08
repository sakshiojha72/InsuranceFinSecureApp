package com.ds.app.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MyUserDetails implements UserDetails{

	private AppUser user;

	public MyUserDetails(AppUser user) {
		super();
		this.user = user;
	}
	public AppUser getUser()
	{
		return this.user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
<<<<<<< HEAD
	    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
=======
		SimpleGrantedAuthority a = new SimpleGrantedAuthority(user.getRole().toString());

		return Arrays.asList(a);
>>>>>>> 7289d7ba89146c2d04f97485d8104964269482a7
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
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
	public AppUser getUser()
	{
		return this.user;
	}
	
}
