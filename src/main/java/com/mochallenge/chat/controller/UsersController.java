package com.mochallenge.chat.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mochallenge.chat.controller.dto.GetListOfUsersResponse;
import com.mochallenge.chat.controller.dto.GetUserResponse;
import com.mochallenge.chat.controller.dto.LoginUserRequest;
import com.mochallenge.chat.controller.dto.LoginUserResponse;
import com.mochallenge.chat.controller.dto.RegisterUserRequest;
import com.mochallenge.chat.controller.dto.RegisterUserResponse;
import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.mapper.SecurityMapper;
import com.mochallenge.chat.mapper.UserMapper;
import com.mochallenge.chat.service.SecurityService;
import com.mochallenge.chat.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final SecurityService securityService;
    private final UserMapper userMapper;
    private final SecurityMapper securityMapper;

    @PostMapping("/register")
    public RegisterUserResponse registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        User user = securityService.registerUser(registerUserRequest.getUserName(), registerUserRequest.getPassword());
        return userMapper.toRegisterUserResponse(user);
    }

    @PostMapping("/login")
    public LoginUserResponse loginUser(@RequestBody LoginUserRequest loginUserRequest) {
        SecurityService.UserLoggedInModel userLoggedInModel =
                securityService.login(loginUserRequest.getUserName(), loginUserRequest.getPassword());
        return securityMapper.toLoginUserResponse(userLoggedInModel);
    }

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
