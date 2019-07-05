package com.mochallenge.chat.service.message;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.bot.ChatBot;
import com.mochallenge.chat.domain.Room;
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
    private final List<ChatBot> chatBots;

    // FIXME: refactor to SendMessageCommand
    public void sendMessage(String userId, String roomId, String message) {
        userService.getUser(userId);
        Room room = roomService.getRoom(roomId);
        if (!CollectionUtils.containsAny(room.getUsers(), userId)) {
            throw new ChatValidationException("User with id [" + userId + "] does not belong to room with id [" + roomId + "]");
        }
        ChatEvent messageSentEvent = new MessageSentEvent(userId, roomId, message);
        eventPublisher.publishEvent(messageSentEvent, room.getUsers());

        for (ChatBot bot : CollectionUtils.emptyIfNull(chatBots)) {
            bot.processEvent(messageSentEvent).ifPresent(event -> eventPublisher.publishEvent(event, room.getUsers()));
        }
    }
}
