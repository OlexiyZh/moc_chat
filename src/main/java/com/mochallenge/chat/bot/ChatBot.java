package com.mochallenge.chat.bot;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.mochallenge.chat.service.message.event.ChatEvent;

public interface ChatBot {
    CompletableFuture<Optional<ChatEvent>> processEvent(ChatEvent event);
}
