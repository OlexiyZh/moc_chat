package com.mochallenge.chat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.mochallenge.chat.controller.dto.LoginUserResponse;
import com.mochallenge.chat.service.SecurityService;

@Mapper
public abstract class ChatSecurityMapper {

    @Mappings({
            @Mapping(target = "userId", source = "user.userId"),
            @Mapping(target = "userName", source = "user.userName"),
            @Mapping(target = "credentials", source = "credentials")
    })
    public abstract LoginUserResponse toLoginUserResponse(SecurityService.UserLoggedInModel userLoggedInModel);

}
