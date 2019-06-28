package com.mochallenge.chat.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mochallenge.chat.controller.dto.GetListOfUsersResponse;
import com.mochallenge.chat.controller.dto.GetUserResponse;
import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.mapper.UserMapper;
import com.mochallenge.chat.service.UserService;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(description = "Operations for users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GetListOfUsersResponse getListOfUsers() {
        List<User> users = userService.getAllUsers();
        return userMapper.toGetListOfUsersResponse(users);
    }

    @GetMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GetUserResponse getUser(@PathVariable("userId") String userId) {
        User user = userService.getUser(userId);
        return userMapper.toGetUserResponse(user);
    }
}
