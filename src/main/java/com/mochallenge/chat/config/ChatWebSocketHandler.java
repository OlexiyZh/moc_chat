package com.mochallenge.chat.config;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mochallenge.chat.service.message.IEventPublisher;
import com.mochallenge.chat.service.message.event.ChatEvent;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler implements IEventPublisher {

    private final ObjectMapper objectMapper;

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap();

    public void afterConnectionEstablished(WebSocketSession session) {
        String xUserId = getUserId(session);
        sessions.put(xUserId, session);
    }


    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String xUserId = getUserId(session);
        sessions.remove(xUserId);
    }

    @Override
    @SneakyThrows
    public void publicEvent(ChatEvent event, List<String> subscribers) {
        for (String subscriber : subscribers) {

            if (sessions.containsKey(subscriber)) {
                WebSocketSession session = sessions.get(subscriber);
                session.sendMessage(prepareMessage(event));
            }
        }
    }

    @SneakyThrows
    private TextMessage prepareMessage(ChatEvent event) {
        return new TextMessage(objectMapper.writeValueAsString(event));
    }

    private String getUserId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        //TODO: validate session
        return query.replaceFirst("x-user-id=", "");
    }
}
