package com.mochallenge.chat.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.mochallenge.chat.controller.dto.CreateRoomResponse;
import com.mochallenge.chat.controller.dto.GetListOfRoomsResponse;
import com.mochallenge.chat.controller.dto.GetRoomResponse;
import com.mochallenge.chat.domain.Room;
import com.mochallenge.chat.domain.User;

@Mapper(uses = UserMapper.class)
public abstract class RoomMapper {

    public GetListOfRoomsResponse toGetListOfRoomsResponse(List<Room> rooms, Map<String, List<User>> roomToUsers){
        GetListOfRoomsResponse getListOfRoomsResponse = new GetListOfRoomsResponse();
        getListOfRoomsResponse.setRooms(toGetRoomResponseList(rooms, roomToUsers));
        return getListOfRoomsResponse;
    }

    public List<GetRoomResponse> toGetRoomResponseList(List<Room> rooms, Map<String, List<User>> roomToUsers) {
        if (rooms == null) {
            return null;
        }
        roomToUsers = MapUtils.emptyIfNull(roomToUsers);

        List<GetRoomResponse> getRoomResponseList = new ArrayList(rooms.size());
        Iterator<Room> roomsIterator = rooms.iterator();

        while (roomsIterator.hasNext()) {
            Room room = roomsIterator.next();
            List<User> users = roomToUsers.get(room.getRoomId());
            getRoomResponseList.add(this.toGetRoomResponse(room, ListUtils.emptyIfNull(users)));
        }

        return getRoomResponseList;
    }

    @Mappings({
            @Mapping(target = "roomId", source = "room.roomId"),
            @Mapping(target = "roomName", source = "room.roomName"),
            @Mapping(target = "creatorId", source = "room.creatorId"),
            @Mapping(target = "createdAt", source = "room.createdAt"),
            @Mapping(target = "users", source = "users")
    })
    public abstract GetRoomResponse toGetRoomResponse(Room room, List<User> users);

    @Mappings({
            @Mapping(target = "roomId", source = "roomId"),
            @Mapping(target = "roomName", source = "roomName"),
            @Mapping(target = "creatorId", source = "creatorId"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "users", ignore = true)
    })
    public abstract CreateRoomResponse toCreateRoomResponse(Room room);

}
