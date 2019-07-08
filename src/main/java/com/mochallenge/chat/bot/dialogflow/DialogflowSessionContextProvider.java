package com.mochallenge.chat.bot.dialogflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(DialogflowBot.class)
public class DialogflowSessionContextProvider {

    private static final String DEFAULT_LANGUAGE_CODE = "en-US";

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
        // TODO: Validate parameters and context
    }
}
