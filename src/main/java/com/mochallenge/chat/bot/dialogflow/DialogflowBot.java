package com.mochallenge.chat.bot.dialogflow;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<Optional<ChatEvent>> processEvent(ChatEvent event) {
        return CompletableFuture.supplyAsync(() -> this.processEventInternal(event));
    }

    private Optional<ChatEvent> processEventInternal(ChatEvent event) {
        if (!shouldRespond(event)) {
            return Optional.empty();
        }

        String response = processIncomeEvent(event);

        return StringUtils.isBlank(response) ?
                Optional.empty() :
                Optional.of(new MessageSentEvent(BOT_ALIAS, event.getRoomId(), response));
    }

    private String replaceBotNameFromMessage(String message) {
        return message.replaceFirst(BOT_ALIAS_REPLACEMENT_REGEX, "");
    }

    private boolean shouldRespond(ChatEvent event) {
        return StringUtils.startsWith(event.getMessage(), BOT_ALIAS);
    }

    private String processIncomeEvent(ChatEvent event) {
        String message = replaceBotNameFromMessage(event.getMessage());
        String sessionId = DialogflowUtils.getSessionId(event);
        DialogflowSessionContext context = dialogflowSessionContextProvider.getContext(sessionId);

        // TODO: Refactor this code
        String response;
        if (DialogflowSessionContextProvider.ENGLISH_LANGUAGE_CODE.equals(context.getLanguageCode())
                && message.toLowerCase().contains("switch to ukrainian")) {
            context = context.withLanguageCode(DialogflowSessionContextProvider.UKRAINIAN_LANGUAGE_CODE);
            dialogflowSessionContextProvider.updateContext(context);
            response = "Гаразд. Далі українською";
        } else if (DialogflowSessionContextProvider.UKRAINIAN_LANGUAGE_CODE.equals(context.getLanguageCode())
                && message.toLowerCase().contains("перейти на англійську")) {
            context = context.withLanguageCode(DialogflowSessionContextProvider.ENGLISH_LANGUAGE_CODE);
            dialogflowSessionContextProvider.updateContext(context);
            response = "Ok. I will use English now";
        } else {
            response = dialogflowAgentClient.sendTextMessage(context, message);
        }

        return response;
    }

}
