package com.mochallenge.chat.bot.context;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.mochallenge.chat.bot.dialogflow.DialogflowBot;

import lombok.NonNull;

@Component
@ConditionalOnBean(DialogflowBot.class)
public class UserContextProvider {

    // TODO: Expire session after some time
    private Map<String, UserContext> contexts = new ConcurrentHashMap();

    public UserContext getContext(@NonNull String userId) {
        UserContext context = contexts.get(userId);
        if (context == null) {
            context = UserContext.builder().userId(userId).orders(Collections.emptyList()).build();
            contexts.put(userId, context);
        }

        return context;
    }

    public void updateContext(@NonNull UserContext context) {
        contexts.put(context.getUserId(), context);
    }
}
