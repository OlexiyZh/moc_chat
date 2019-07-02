package com.mochallenge.chat.bot;

import java.util.Optional;

import com.mochallenge.chat.service.message.event.ChatEvent;

public interface ChatBot {
    Optional<ChatEvent> processEvent(ChatEvent event);
}
