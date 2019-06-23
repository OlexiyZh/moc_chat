package com.mochallenge.chat.service.message.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class MessageSentEvent implements ChatEvent {
    private final String userId;
    private final String roomId;
    private final String message;
}
