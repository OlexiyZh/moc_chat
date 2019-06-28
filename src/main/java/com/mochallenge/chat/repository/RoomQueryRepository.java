package com.mochallenge.chat.repository;

import org.springframework.stereotype.Component;

import com.mochallenge.chat.domain.RoomQuery;

@Component
public interface RoomQueryRepository extends ReadOnlyRepository<RoomQuery, String> {
}
