package com.mochallenge.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mochallenge.chat.controller.dto.AddUserToRoomRequest;
import com.mochallenge.chat.controller.dto.CreateRoomRequest;
import com.mochallenge.chat.controller.dto.CreateRoomResponse;
import com.mochallenge.chat.controller.dto.GetListOfRoomsResponse;
import com.mochallenge.chat.controller.dto.GetRoomResponse;
import com.mochallenge.chat.controller.dto.RemoveUserFromRoomRequest;
import com.mochallenge.chat.controller.dto.StringResultResponse;
import com.mochallenge.chat.domain.Room;
import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.mapper.RoomMapper;
import com.mochallenge.chat.service.RoomService;
import com.mochallenge.chat.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomsController {

    private final RoomService roomService;
    private final UserService userService;
    private final RoomMapper roomMapper;

    @GetMapping()
    public GetListOfRoomsResponse getListOfRooms() {
        List<Room> rooms = roomService.getAllRooms();
        /* TODO: Refactor this
        * maybe introduce new Entity with mapping on User just for PL layer
        */
        Map<String, List<User>> roomsToUsers = new HashMap<>();
        for(Room room : rooms) {
            roomsToUsers.put(room.getRoomId(), userService.getUsersWithIds(room.getUsers()));
        }
        return roomMapper.toGetListOfRoomsResponse(rooms, roomsToUsers);
    }

    @GetMapping("/{roomId}")
    public GetRoomResponse getRoom(@PathVariable("roomId") String roomId) {
        Room room = roomService.getRoom(roomId);
        List<User> users = userService.getUsersWithIds(room.getUsers());
        return roomMapper.toGetRoomResponse(room, users);
    }

    @PostMapping()
    public CreateRoomResponse createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        Room room = roomService.createRoom(createRoomRequest.getName(), createRoomRequest.getUserId());
        return roomMapper.toCreateRoomResponse(room);
    }

    @DeleteMapping("/{roomId}")
    public StringResultResponse removeRoom(@PathVariable("roomId") String roomId) {
        roomService.removeRoom(roomId);
        return new StringResultResponse("Room with id [" + roomId + "] was removed successfully");
    }

    @PostMapping("/{roomId}/join")
    public StringResultResponse addUserToRoom(@PathVariable("roomId") String roomId,
                                              @RequestBody AddUserToRoomRequest addUserToRoomRequest) {
        roomService.addUserToRoom(roomId, addUserToRoomRequest.getUserId());
        return new StringResultResponse("User with id [" + addUserToRoomRequest.getUserId() +
                "] was added to room with id [" + roomId + "]");
    }

    @PostMapping("/{roomId}/leave")
    public StringResultResponse removeUserFromRoom(@PathVariable("roomId") String roomId,
                                                   @RequestBody RemoveUserFromRoomRequest removeUserFromRoomRequest) {
        roomService.removeUserFromRoom(roomId, removeUserFromRoomRequest.getUserId());
        return new StringResultResponse("User with id [" + removeUserFromRoomRequest.getUserId() +
                "] was removed from room with id [" + roomId + "]");
    }
}
