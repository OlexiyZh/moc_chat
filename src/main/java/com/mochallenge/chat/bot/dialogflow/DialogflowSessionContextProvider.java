package com.mochallenge.chat.bot.dialogflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(DialogflowBot.class)
public class DialogflowSessionContextProvider {

    public static final String ENGLISH_LANGUAGE_CODE = "en-US";
    public static final String UKRAINIAN_LANGUAGE_CODE = "uk";

    private static final String DEFAULT_LANGUAGE_CODE = ENGLISH_LANGUAGE_CODE;

    private Map<String, DialogflowSessionContext> contexts = new ConcurrentHashMap();

    public DialogflowSessionContext getContext(String sessionId) {
        // TODO: Validate parameters
        DialogflowSessionContext context = contexts.get(sessionId);
        if (context == null) {
            context = new DialogflowSessionContext(sessionId, DEFAULT_LANGUAGE_CODE);
            contexts.put(sessionId, context);
        }

        return context;
    }

    public void updateContext(DialogflowSessionContext context) {
        // TODO: Validate parameter
        contexts.put(context.getSessionId(), context);
    }
}
