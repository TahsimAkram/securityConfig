package com.bj.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface SecurityUserService {
	UserDetailsService userDetailsService();
}

