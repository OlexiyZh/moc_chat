package com.mochallenge.chat.config;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mochallenge.chat.service.message.EventPublisher;
import com.mochallenge.chat.service.message.event.ChatEvent;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler implements EventPublisher {

    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("(?<=x-user-id=)[^&]+");

    private final ObjectMapper objectMapper;

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap();

    @SneakyThrows
    public void afterConnectionEstablished(WebSocketSession session) {
        String xUserId = getUserId(session);
        if(StringUtils.isBlank(xUserId)) {
            // close session because mandatory parameter is missing
            session.close(CloseStatus.BAD_DATA);
        }
        sessions.put(xUserId, session);
    }


    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String xUserId = getUserId(session);
        // if session does not contains xUserId query param that means
        // we did not put this session in map
        if(StringUtils.isNotBlank(xUserId)) {
            sessions.remove(xUserId);
        }
    }

    private String getUserId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (StringUtils.isBlank(query)) {
            return null;
        }
        Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
        if(!matcher.find()) {
            return null;
        }
        return matcher.group();
    }

    @Override
    public void publishEvent(ChatEvent event, List<String> recipients) {
        for (String recipient : recipients) {
            if (sessions.containsKey(recipient)) {
                WebSocketSession session = sessions.get(recipient);
                try {
                    session.sendMessage(buildTextMessage(event));
                } catch (IOException e) {
                    log.error("Error sending message to user with id [" + recipient + "]", e);
                }
            }
        }
    }

    @SneakyThrows
    private TextMessage buildTextMessage(ChatEvent event) {
        return new TextMessage(objectMapper.writeValueAsString(event));
    }

}
