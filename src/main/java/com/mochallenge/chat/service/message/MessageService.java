package com.mochallenge.chat.service.message;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.domain.Room;
import com.mochallenge.chat.domain.User;
import com.mochallenge.chat.exception.ChatValidationException;
import com.mochallenge.chat.service.RoomService;
import com.mochallenge.chat.service.UserService;
import com.mochallenge.chat.service.message.event.ChatEvent;
import com.mochallenge.chat.service.message.event.MessageSentEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageService {

    private final UserService userService;
    private final RoomService roomService;
    private final EventPublisher eventPublisher;

    // FIXME: refactor to SendMessageCommand
    public void sendMessage(String userId, String roomId, String message) {
        User user = userService.getUser(userId);
        Room room = roomService.getRoom(roomId);

        if (!CollectionUtils.containsAny(room.getUsers(), userId)) {
            throw new ChatValidationException("User with id [" + userId + "] does not belong to room with id [" + roomId + "]");
        }

        ChatEvent messageSentEvent = new MessageSentEvent(userId, roomId, message);
        eventPublisher.publicEvent(messageSentEvent);
    }
}
