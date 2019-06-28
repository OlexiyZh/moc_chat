package com.mochallenge.chat.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetRoomResponse {
    private String roomId;
    private String roomName;
    private String creatorId;
    private LocalDateTime createdAt;
    private List<GetUserResponse> users;

}
