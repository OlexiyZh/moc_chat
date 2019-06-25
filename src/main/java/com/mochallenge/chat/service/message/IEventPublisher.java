package com.mochallenge.chat.service.message;

import java.util.List;

import com.mochallenge.chat.service.message.event.ChatEvent;

public interface IEventPublisher {
    void publicEvent(ChatEvent event, List<String> subscribers);
}
