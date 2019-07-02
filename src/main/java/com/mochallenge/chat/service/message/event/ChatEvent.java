package com.mochallenge.chat.service.message.event;

public interface ChatEvent {
    String getUserId();
    String getRoomId();
    String getMessage();
}
