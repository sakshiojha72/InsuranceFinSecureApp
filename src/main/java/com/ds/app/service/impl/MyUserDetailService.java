package com.ds.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.repository.iAppUserRepository;

@Service
public class MyUserDetailService implements UserDetailsService {

	
	@Autowired
	private iAppUserRepository appUserRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		AppUser user = appUserRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
		return new MyUserDetails(user);
	}

}
