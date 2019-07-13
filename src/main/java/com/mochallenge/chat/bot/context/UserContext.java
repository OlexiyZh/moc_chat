package com.mochallenge.chat.bot.context;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@Getter
@Builder
@Wither
@ToString
@RequiredArgsConstructor
public class UserContext {

    private final String userId;
    @Singular
    private final List<UserOrder> orders;
}
