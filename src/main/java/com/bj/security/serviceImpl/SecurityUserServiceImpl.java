package com.bj.security.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.bj.security.dto.User;
import com.bj.security.entity.UserEntity;
import com.bj.security.repository.UserDetailRepository;
import com.bj.security.service.SecurityUserService;
@Service
public class SecurityUserServiceImpl implements SecurityUserService {

	@Autowired
	UserDetailRepository userRepository;
	
	@Value("${bcrypt.default.password}")
	private String defaultPassword;

	@Override
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			public User loadUserByUsername(String userId) {
				//return userDao.findByUserLogin(userLogin);
				UserEntity entity = userRepository.findByUserId(Integer.valueOf(userId)).get();
				User usr = new User();
				usr.setUserId(entity.getUserId());
				usr.setUserlogin(String.valueOf(entity.getUserId()));
				usr.setPassword(defaultPassword);
				usr.setRole(entity.getRole());
				List<String> userRoles = new ArrayList<String>();
				userRoles.add(entity.getRole());
				usr.setSalutation(entity.getSalutation());
				usr.setFirstname(entity.getFirstName());
				usr.setLastname(entity.getLastName());
				usr.setEmail(entity.getEmail());
				usr.setMobile(entity.getMobile());
				return usr;
			}
		};
	}

}
