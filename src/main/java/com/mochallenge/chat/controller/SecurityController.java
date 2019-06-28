package com.mochallenge.chat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mochallenge.chat.controller.dto.LoginUserRequest;
import com.mochallenge.chat.controller.dto.LoginUserResponse;
import com.mochallenge.chat.controller.dto.RegisterUserRequest;
import com.mochallenge.chat.controller.dto.RegisterUserResponse;
import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.mapper.SecurityMapper;
import com.mochallenge.chat.mapper.UserMapper;
import com.mochallenge.chat.service.SecurityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SecurityController {

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
}
