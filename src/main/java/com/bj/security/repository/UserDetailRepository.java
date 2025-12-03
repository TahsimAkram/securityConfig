package com.bj.security.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bj.security.entity.UserEntity;
import com.bj.security.util.SQLQueries;

@Repository
public class UserDetailRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

	public Optional<UserEntity> findByUserId(Integer userId) {
		    
		    try {
		    	UserEntity user = jdbcTemplate.queryForObject(
		        	SQLQueries.FIND_USER_USING_USERID,
		            new Object[]{userId},
		            new BeanPropertyRowMapper<>(UserEntity.class)
		        );
		        return Optional.ofNullable(user);
		    } catch (Exception e) {
		    	e.printStackTrace();
		        return Optional.empty();
		    }
		}
	
	public Optional<UserEntity> findByUsernameAndPassword(String username, String password) {
	    try {
	    	UserEntity user = jdbcTemplate.queryForObject(
	            SQLQueries.FIND_USER_USING_USERNAME_PASSWORD,
	            new Object[]{username, password},
	            new BeanPropertyRowMapper<>(UserEntity.class)
	        );
	        return Optional.ofNullable(user);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return Optional.empty();
	    }
	}
}
