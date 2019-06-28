package com.mochallenge.chat.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mochallenge.chat.controller.dto.LoginUserRequest;
import com.mochallenge.chat.controller.dto.LoginUserResponse;
import com.mochallenge.chat.controller.dto.RegisterUserRequest;
import com.mochallenge.chat.controller.dto.RegisterUserResponse;
import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.mapper.ChatSecurityMapper;
import com.mochallenge.chat.mapper.UserMapper;
import com.mochallenge.chat.service.SecurityService;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(description = "Security operations for users")
@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;
    private final UserMapper userMapper;
    private final ChatSecurityMapper chatSecurityMapper;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RegisterUserResponse registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        User user = securityService.registerUser(registerUserRequest.getUserName(), registerUserRequest.getPassword());
        return userMapper.toRegisterUserResponse(user);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LoginUserResponse loginUser(@RequestBody LoginUserRequest loginUserRequest) {
        SecurityService.UserLoggedInModel userLoggedInModel =
                securityService.login(loginUserRequest.getUserName(), loginUserRequest.getPassword());
        return chatSecurityMapper.toLoginUserResponse(userLoggedInModel);
    }
}
