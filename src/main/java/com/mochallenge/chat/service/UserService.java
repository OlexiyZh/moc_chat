package com.mochallenge.chat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.exception.ObjectNotFoundException;
import com.mochallenge.chat.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean isUserExist(String userId) {
        return userRepository.existsById(userId);
    }

    public User getUser(String id) {

        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new ObjectNotFoundException("User with id [" + id + "] not found");
        }
        return user.get();
    }

    public User getUserByUserName(String userName) {

        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()) {
            throw new ObjectNotFoundException("User with userName [" + userName + "] not found");
        }
        return user.get();
    }

    public List<User> getAllUsers() {
        return Lists.newArrayList(userRepository.findAll());
    }

}
