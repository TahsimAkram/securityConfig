package com.bj.security.service;

import com.bj.security.entity.UserEntity;

public interface JWTService {
	String extractSubject(String token);
	public boolean validateToken(String token);
	public String generateToken(UserEntity user);
}
