package com.mochallenge.chat.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.exception.ChatValidationException;
import com.mochallenge.chat.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final PasswordEncoder passwordEncoder;
    private final SecurityValidationService securityValidationService;
    private final UserRepository userRepository;

    public User registerUser(String userName, String userPassword) {

        securityValidationService.validate(userName, userPassword);
        if (userRepository.existsByUserName(userName)) {
            throw new ChatValidationException("User with name [" + userName + "] already exists");
        }
        User user = new User();
        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(userPassword));
        return userRepository.save(user);
    }

    public String login(String userName, String userPassword) {
        String plainClientCredentials=userName + ":" + userPassword;
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        return base64ClientCredentials;
    }
}
