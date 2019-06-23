package com.mochallenge.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.mochallenge.chat.controller.dto.CreateRoomResponse;
import com.mochallenge.chat.controller.dto.GetListOfRoomsResponse;
import com.mochallenge.chat.controller.dto.GetRoomResponse;
import com.mochallenge.chat.domain.Room;

@Mapper
public abstract class RoomMapper {

    public GetListOfRoomsResponse toGetListOfRoomsResponse(List<Room> rooms){
        GetListOfRoomsResponse getListOfRoomsResponse = new GetListOfRoomsResponse();
        getListOfRoomsResponse.setRooms(toGetRoomResponseList(rooms));
        return getListOfRoomsResponse;
    }

    public abstract List<GetRoomResponse> toGetRoomResponseList(List<Room> rooms);

    @Mappings({
            @Mapping(target = "roomId", source = "roomId"),
            @Mapping(target = "roomName", source = "roomName"),
            @Mapping(target = "creatorId", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "users", ignore = true)
    })
    public abstract GetRoomResponse toGetRoomResponse(Room room);

    @Mappings({
            @Mapping(target = "roomId", source = "roomId"),
            @Mapping(target = "roomName", source = "roomName"),
            @Mapping(target = "creatorId", source = "creatorId"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "users", ignore = true)
    })
    public abstract CreateRoomResponse toCreateRoomResponse(Room room);

}
