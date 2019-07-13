package com.mochallenge.chat.bot.notify;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.service.message.EventPublisher;
import com.mochallenge.chat.service.message.event.ChatEvent;
import com.mochallenge.chat.service.message.event.MessageSentEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotifyService {

    private final String NOTIFY_SERVICE_ID = "[NT_SERVICE]";

    @Value("${chat.notification.delayInSeconds}")
    private int delayInSeconds;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final EventPublisher eventPublisher;


    public void notifyUser(String roomId, String userId, String message) {
        ChatEvent event = new MessageSentEvent(NOTIFY_SERVICE_ID, roomId, message);

        scheduler.schedule(() -> eventPublisher.publishEvent(event, Arrays.asList(userId)),
                delayInSeconds, TimeUnit.SECONDS);
        scheduler.shutdown();
    }
}
