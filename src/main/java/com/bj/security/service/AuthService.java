package com.bj.security.service;

public interface AuthService {
	 String authenticateUser(String username, String password) throws Exception;
}
