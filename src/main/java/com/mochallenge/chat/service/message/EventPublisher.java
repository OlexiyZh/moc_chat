package com.mochallenge.chat.service.message;

import java.util.List;

import com.mochallenge.chat.service.message.event.ChatEvent;

public interface EventPublisher {
    void publishEvent(ChatEvent event, List<String> recipients);
}
