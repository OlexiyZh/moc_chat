package com.mochallenge.chat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mochallenge.chat.controller.dto.SendMessageRequest;
import com.mochallenge.chat.controller.dto.StringResultResponse;
import com.mochallenge.chat.service.message.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping()
    public StringResultResponse sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
        messageService.sendMessage(sendMessageRequest.getUserId(),
                                   sendMessageRequest.getRoomId(),
                                   sendMessageRequest.getMessage());
        return new StringResultResponse("Message was sent");
    }

}
