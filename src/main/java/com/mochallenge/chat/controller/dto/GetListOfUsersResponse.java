package com.mochallenge.chat.controller.dto;


import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetListOfUsersResponse {
    private List<GetUserResponse> users;
}
