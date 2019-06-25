package com.mochallenge.chat.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.exception.ChatValidationException;

@Component
public class SecurityValidationService {

    void validate(String userName, String userPassword) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(userPassword)) {
            throw new ChatValidationException("UserName or password is blank");
        }
    }
}
