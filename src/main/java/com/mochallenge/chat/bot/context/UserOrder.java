package com.mochallenge.chat.bot.context;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Wither;

@Getter
@Wither
@ToString
public class UserOrder {

    public UserOrder(String service, String dateTime) {
        //this.userOrderId = UUID.randomUUID().toString();
        // FIXME: Dialogflow does not work with UUID
        this.userOrderId = String.valueOf(System.currentTimeMillis());
        this.service = service;
        this.dateTime = dateTime;
    }

    public UserOrder(String userOrderId, String service, String dateTime) {
        this.userOrderId = userOrderId;
        this.service = service;
        this.dateTime = dateTime;
    }

    private final String userOrderId;

    private final String service;

    // TODO: Change to Joda dateTime
    private final String dateTime;
}
