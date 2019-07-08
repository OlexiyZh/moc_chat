package com.mochallenge.chat.bot.dialogflow;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.bot.ChatBot;
import com.mochallenge.chat.service.message.event.ChatEvent;
import com.mochallenge.chat.service.message.event.MessageSentEvent;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(value="chat.bot.dialogflow.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DialogflowBot implements ChatBot {

    private final static String BOT_ALIAS = "@bot";
    private final static String BOT_ALIAS_REPLACEMENT_REGEX = BOT_ALIAS + " ?";

    private final DialogflowSessionContextProvider dialogflowSessionContextProvider;
    private final DialogflowAgentClient dialogflowAgentClient;

    @Override
    public Optional<ChatEvent> processEvent(ChatEvent event) {
        if (!shouldRespond(event)) {
            return Optional.empty();
        }

        String message = replaceBotNameFromMessage(event.getMessage());
        String sessionId = DialogflowUtils.getSessionId(event);
        DialogflowSessionContext context = dialogflowSessionContextProvider.getContext(sessionId);

        String agentResponse = dialogflowAgentClient.sendTextMessage(context, message);

        return StringUtils.isBlank(agentResponse) ?
                Optional.empty() :
                Optional.of(new MessageSentEvent(BOT_ALIAS, event.getRoomId(), agentResponse));
    }

    private String replaceBotNameFromMessage(String message) {
        return message.replaceFirst(BOT_ALIAS_REPLACEMENT_REGEX, "");
    }

    private boolean shouldRespond(ChatEvent event) {
        return StringUtils.startsWith(event.getMessage(), BOT_ALIAS);
    }

}
