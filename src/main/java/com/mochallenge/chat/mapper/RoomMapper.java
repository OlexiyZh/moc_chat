package com.mochallenge.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.mochallenge.chat.controller.dto.CreateRoomResponse;
import com.mochallenge.chat.controller.dto.GetListOfRoomsResponse;
import com.mochallenge.chat.controller.dto.GetRoomResponse;
import com.mochallenge.chat.domain.Room;
import com.mochallenge.chat.domain.RoomQuery;

@Mapper(uses = UserMapper.class)
public abstract class RoomMapper {

    public GetListOfRoomsResponse toGetListOfRoomsResponse(List<RoomQuery> roomQueries){
        GetListOfRoomsResponse getListOfRoomsResponse = new GetListOfRoomsResponse();
        getListOfRoomsResponse.setRooms(toGetRoomResponseList(roomQueries));
        return getListOfRoomsResponse;
    }

    public abstract List<GetRoomResponse> toGetRoomResponseList(List<RoomQuery> roomQueries);

    @Mappings({
            @Mapping(target = "roomId", source = "roomId"),
            @Mapping(target = "roomName", source = "roomName"),
            @Mapping(target = "creatorId", source = "creatorId"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "users", source = "users")
    })
    public abstract GetRoomResponse toGetRoomResponse(RoomQuery room);

    @Mappings({
            @Mapping(target = "roomId", source = "roomId"),
            @Mapping(target = "roomName", source = "roomName"),
            @Mapping(target = "creatorId", source = "creatorId"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "users", ignore = true)
    })
    public abstract CreateRoomResponse toCreateRoomResponse(Room room);

}
