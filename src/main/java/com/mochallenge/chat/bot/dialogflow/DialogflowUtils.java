package com.mochallenge.chat.bot.dialogflow;

import com.mochallenge.chat.service.message.event.ChatEvent;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DialogflowUtils {

    public static String getSessionId(ChatEvent event) {
        // TODO: Refactor this logic
        return event.getRoomId();
    }
}
