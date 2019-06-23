package com.mochallenge.chat.service.message;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.service.message.event.ChatEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final SimpMessageSendingOperations messagingTemplate;

    public void publicEvent(ChatEvent event) {
        messagingTemplate.convertAndSend("/topic", event);
    }
}
