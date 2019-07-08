package com.mochallenge.chat.bot.dialogflow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DialogflowSessionContext {

    private final String sessionId;
    private final String languageCode;
}
