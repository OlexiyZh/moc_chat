package com.mochallenge.chat.bot;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.service.message.event.ChatEvent;
import com.mochallenge.chat.service.message.event.MessageSentEvent;

@Component
@ConditionalOnProperty(value="chat.bot.echobot.enabled", havingValue = "true")
public class EchoBot implements ChatBot {

    private final static String BOT_ALIAS = "@echobot";
    private final static String BOT_ALIAS_REPLACEMENT_REGEX = BOT_ALIAS + " ?";

    @Override
    public CompletableFuture<Optional<ChatEvent>> processEvent(ChatEvent event) {
        return CompletableFuture.supplyAsync(() -> this.processEventInternal(event));
    }

    private Optional<ChatEvent> processEventInternal(ChatEvent event) {
        if (!shouldRespond(event)) {
            return Optional.empty();
        }
        return Optional.of(buildResponseMessage(event));
    }

    private ChatEvent buildResponseMessage(ChatEvent event) {
        String message = event.getMessage().replaceFirst(BOT_ALIAS_REPLACEMENT_REGEX, "");
        String responseMessage = String.format("User said '%s'", message);
        return new MessageSentEvent(BOT_ALIAS, event.getRoomId(), responseMessage);
    }

    private boolean shouldRespond(ChatEvent event) {
        return StringUtils.startsWith(event.getMessage(), BOT_ALIAS);
    }

}
