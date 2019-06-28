package com.mochallenge.chat.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mochallenge.chat.controller.dto.GetListOfUsersResponse;
import com.mochallenge.chat.controller.dto.GetUserResponse;
import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.mapper.UserMapper;
import com.mochallenge.chat.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping()
    public GetListOfUsersResponse getListOfUsers() {
        List<User> users = userService.getAllUsers();
        return userMapper.toGetListOfUsersResponse(users);
    }

    @GetMapping("/{userId}")
    public GetUserResponse getUser(@PathVariable("userId") String userId) {
        User user = userService.getUser(userId);
        return userMapper.toGetUserResponse(user);
    }
}
