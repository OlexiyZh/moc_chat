package com.mochallenge.chat.bot;

import java.util.Optional;

import com.mochallenge.chat.service.message.event.ChatEvent;

public interface ChatBot {

    // TODO: Make this method not blocking
    // It's critical for DialogflowBot
    Optional<ChatEvent> processEvent(ChatEvent event);
}
