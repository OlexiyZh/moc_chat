package com.mochallenge.chat.service;

import java.util.Optional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.exception.ChatValidationException;
import com.mochallenge.chat.exception.ObjectNotFoundException;
import com.mochallenge.chat.repository.UserRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    public UserLoggedInModel login(String userName, String userPassword) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException("User with userName [" + userName + "] not found");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            throw new ChatValidationException("Incorrect password for user with name [" + userName + "]");
        }
        return new UserLoggedInModel(user, generateBase64ClientCredentials(userName, userPassword));
    }

    private String generateBase64ClientCredentials(String userName, String userPassword) {
        String plainClientCredentials = userName + ":" + userPassword;
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        return base64ClientCredentials;
    }

    @Getter
    @Setter
    @ToString
    @RequiredArgsConstructor
    public class UserLoggedInModel{
        private final User user;
        private final String credentials;
    }
}
