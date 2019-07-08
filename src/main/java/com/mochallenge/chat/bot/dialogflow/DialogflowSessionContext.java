package com.mochallenge.chat.bot.dialogflow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

@Getter
@Wither
@RequiredArgsConstructor
public class DialogflowSessionContext {

    private final String sessionId;
    private final String languageCode;
}
