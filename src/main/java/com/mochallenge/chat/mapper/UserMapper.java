package com.mochallenge.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.mochallenge.chat.controller.dto.GetListOfUsersResponse;
import com.mochallenge.chat.controller.dto.GetUserResponse;
import com.mochallenge.chat.controller.dto.RegisterUserResponse;
import com.mochallenge.chat.domain.User;

@Mapper
public abstract class UserMapper {

    public GetListOfUsersResponse toGetListOfUsersResponse(List<User> users){
        GetListOfUsersResponse getListOfUsersResponse = new GetListOfUsersResponse();
        getListOfUsersResponse.setUsers(toGetUserResponseList(users));
        return getListOfUsersResponse;
    }

    public abstract List<GetUserResponse> toGetUserResponseList(List<User> users);

    @Mappings({
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "userName", source = "userName")
    })
    public abstract GetUserResponse toGetUserResponse(User user);

    @Mappings({
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "userName", source = "userName")
    })
    public abstract RegisterUserResponse toRegisterUserResponse(User user);

}
