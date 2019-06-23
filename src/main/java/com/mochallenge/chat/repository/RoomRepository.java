package com.mochallenge.chat.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.domain.Room;

@Component
public interface RoomRepository extends CrudRepository<Room, String> {
    Optional<Room> findByCreatorId(String creatorId);
}
