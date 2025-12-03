package com.bj.security.serviceImpl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bj.security.entity.UserEntity;
import com.bj.security.repository.UserDetailRepository;
import com.bj.security.service.AuthService;
import com.bj.security.service.JWTService;
import com.bj.security.util.LoginUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JWTService jwtService;
    private final CacheUtilService cacheService;
    private final UserDetailRepository userRepository;

    @Override
    public String authenticateUser(String username, String password) throws Exception {
        String encryptedPassword = LoginUtil.passwordEncrypt(password);
        Optional<UserEntity> userOpt = userRepository.findByUsernameAndPassword(username, encryptedPassword);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        UserEntity user = userOpt.get();
        String token = jwtService.generateToken(user);
        cacheService.delete(user.getUserId().toString());
        cacheService.write(user.getUserId().toString(), token);
//        if (sessionObject != null) {
//            cacheService.saveObject(user.getUserId() + "_sessionObject", sessionObject);
//        }

        return token;
    }
}
