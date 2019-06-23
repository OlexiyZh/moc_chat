package com.mochallenge.chat.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.domain.User;

@Component
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
}
