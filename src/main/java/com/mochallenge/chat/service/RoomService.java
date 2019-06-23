package com.mochallenge.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.mochallenge.chat.domain.Room;
import com.mochallenge.chat.exception.ChatValidationException;
import com.mochallenge.chat.exception.ObjectNotFoundException;
import com.mochallenge.chat.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoomService {

    private final UserService userService;
    private final RoomRepository roomRepository;

    public Room getRoom(String id) {
        Optional<Room> room = roomRepository.findById(id);
        if(!room.isPresent()) {
            throw new ObjectNotFoundException("Room with id [" + id + "] not found");
        }
        return room.get();
    }

    public List<Room> getAllRooms() {
        return Lists.newArrayList(roomRepository.findAll());
    }

    public Room createRoom(String roomName, String creatorId) {
        if(!userService.isUserExist(creatorId)) {
            throw new ObjectNotFoundException("User with id [" + creatorId + "] not found");
        }

        Room room = new Room();
        room.setRoomName(roomName);
        room.setCreatorId(creatorId);

        return roomRepository.save(room);
    }

    public void removeRoom(String roomId) {
        Room room = getRoom(roomId);
        roomRepository.delete(room);
    }

    public void removeRoomByCreatorId(String creatorId) {
        Optional<Room> room = roomRepository.findByCreatorId(creatorId);
        if(!room.isPresent()) {
            throw new ObjectNotFoundException("Room with creator id [" + creatorId + "] not found");
        }
        roomRepository.delete(room.get());
    }

    public void addUserToRoom(String roomId, String userId) {
        Room room = getRoom(roomId);
        if(!userService.isUserExist(userId)) {
            throw new ObjectNotFoundException("User with id [" + userId + "] not found");
        }

        if(room.getUsers() == null) {
            room.setUsers(new ArrayList<>());
        }
        room.getUsers().add(userId);
        roomRepository.save(room);
    }

    public void removeUserFromRoom(String roomId, String userId) {
        Room room = getRoom(roomId);
        if(!userService.isUserExist(userId)) {
            throw new ObjectNotFoundException("User with id [" + userId + "] not found");
        }

        if(!CollectionUtils.containsAny(room.getUsers(), userId)) {
            throw new ChatValidationException("User with id [" + userId + "] does npt belong to room with id [" + roomId + "]");
        }
        room.getUsers().add(userId);
        roomRepository.save(room);
    }
}
