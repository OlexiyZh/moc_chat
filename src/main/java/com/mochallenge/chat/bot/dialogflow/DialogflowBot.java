package com.mochallenge.chat.bot.dialogflow;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.google.cloud.dialogflow.v2.Context;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.common.collect.Lists;
import com.google.protobuf.Value;
import com.mochallenge.chat.bot.ChatBot;
import com.mochallenge.chat.bot.context.UserContext;
import com.mochallenge.chat.bot.context.UserContextProvider;
import com.mochallenge.chat.bot.context.UserOrder;
import com.mochallenge.chat.bot.notify.NotifyService;
import com.mochallenge.chat.service.message.event.ChatEvent;
import com.mochallenge.chat.service.message.event.MessageSentEvent;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(value = "chat.bot.dialogflow.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DialogflowBot implements ChatBot {

    private final static String BOT_ALIAS = "@bot";
    private final static String BOT_ALIAS_REPLACEMENT_REGEX = BOT_ALIAS + " ?";

    private final UserContextProvider userContextProvider;
    private final DialogflowSessionContextProvider dialogflowSessionContextProvider;
    private final DialogflowAgentClient dialogflowAgentClient;
    private final NotifyService notifyService;

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
        } else if ("orders".equals(message)) {
            UserContext userContext = userContextProvider.getContext(event.getUserId());
            response = userContext.toString();
        } else {
            QueryResult queryResult = dialogflowAgentClient.sendTextMessage(context, message);
            response = queryResult.getFulfillmentText();
            Optional<String> commandResponse = processCommands(queryResult, event);
            if (commandResponse.isPresent()) {
                response += "[" + commandResponse.get() + "]";
            }
        }

        return response;
    }

    private Optional<String> processCommands(QueryResult queryResult, ChatEvent event) {

        Optional<String> result = Optional.empty();

        if (queryResult.getIntent().getDisplayName().equals("service.booking.selectDate")) {
            result = processCreateOrderCommand(queryResult, event.getUserId(), event.getRoomId());
        } else if (queryResult.getIntent().getDisplayName().equals("service.booking.cancelOrder")) {
            result = processCancelOrderCommand(queryResult, event.getUserId());
        } else if (queryResult.getIntent().getDisplayName().equals("service.booking.changeOrderDate")) {
            result = processChangeOrderDateCommand(queryResult, event.getUserId());
        }

        return result;
    }

    private Optional<String> processChangeOrderDateCommand(QueryResult queryResult, String userId) {
        List<Context> outputContextsList = queryResult.getOutputContextsList();
        if (outputContextsList.isEmpty()) {
            return Optional.empty();
        }

        Optional<Context> orderDateChangedContextOptional = outputContextsList.stream()
                .filter(context -> context.getName().contains("service_order_date_changed_context")).findFirst();

        if (orderDateChangedContextOptional.isPresent()) {

            Context orderDateChanged = orderDateChangedContextOptional.get();

            Map<String, Value> orderDateChangedParams = orderDateChanged.getParameters().getFieldsMap();
            String orderId = orderDateChangedParams.get("orderId").getStringValue();
            String dateTimeValue = orderDateChangedParams.get("date-time").getStructValue().getFieldsMap().get("date_time").getStringValue();

            UserContext userContext = userContextProvider.getContext(userId);

            Optional<UserOrder> userOrderOptional = userContext.getOrders().stream()
                    .filter(o -> o.getUserOrderId().equals(orderId)).findFirst();

            if (userOrderOptional.isPresent()) {
                // TODO: Return message to user that order does not exist
                UserOrder userOrder = userOrderOptional.get();
                UserOrder updatedUserOrder = userOrder.withDateTime(dateTimeValue);

                List<UserOrder> newOrderList = Lists.newArrayList(userContext.getOrders());
                newOrderList.remove(userOrder);
                newOrderList.add(updatedUserOrder);

                userContext = userContext.withOrders(newOrderList);
                userContextProvider.updateContext(userContext);
            }
        }

        return Optional.empty();
    }

    private Optional<String> processCancelOrderCommand(QueryResult queryResult, String userId) {
        List<Context> outputContextsList = queryResult.getOutputContextsList();
        if (outputContextsList.isEmpty()) {
            return Optional.empty();
        }

        Optional<Context> serviceOrderCancelledContextOptional = outputContextsList.stream()
                .filter(context -> context.getName().contains("service_order_cancelled_context")).findFirst();

        if (serviceOrderCancelledContextOptional.isPresent()) {

            Context serviceOrderCancelledContext = serviceOrderCancelledContextOptional.get();

            String orderId = serviceOrderCancelledContext.getParameters().getFieldsMap().get("orderId").getStringValue();

            UserContext userContext = userContextProvider.getContext(userId);

            if ("all".equals(orderId)) {
                // Remove all user orders
                userContext = userContext.withOrders(Collections.emptyList());
            } else {

                Optional<UserOrder> userOrderOptional = userContext.getOrders().stream()
                        .filter(o -> o.getUserOrderId().equals(orderId)).findFirst();

                if (userOrderOptional.isPresent()) {
                    // TODO: Return message to user that order does not exist
                    UserOrder userOrder = userOrderOptional.get();
                    List<UserOrder> newOrderList = Lists.newArrayList(userContext.getOrders());
                    newOrderList.remove(userOrder);
                    userContext = userContext.withOrders(newOrderList);
                }
            }
            userContextProvider.updateContext(userContext);
        }

        return Optional.empty();
    }

    private Optional<String> processCreateOrderCommand(QueryResult queryResult, String userId, String roomId) {
        List<Context> outputContextsList = queryResult.getOutputContextsList();
        if (outputContextsList.isEmpty()) {
            return Optional.empty();
        }

        Optional<Context> serviceDateSelectedContextOptional = outputContextsList.stream()
                .filter(context -> context.getName().contains("service_date_selected_context")).findFirst();

        Optional<Context> serviceBookedContextOptional = outputContextsList.stream()
                .filter(context -> context.getName().contains("service_booked_context")).findFirst();


        if (serviceDateSelectedContextOptional.isPresent() && serviceBookedContextOptional.isPresent()) {

            Context serviceDateSelectedContext = serviceDateSelectedContextOptional.get();
            Context serviceBookedContext = serviceBookedContextOptional.get();

            String serviceValue = serviceDateSelectedContext.getParameters().getFieldsMap().get("service").getStringValue();
            String dateTimeValue = serviceBookedContext.getParameters().getFieldsMap().get("date-time").getStructValue().getFieldsMap().get("date_time").getStringValue();

            UserContext userContext = userContextProvider.getContext(userId);

            if (userContext.getOrders().stream().anyMatch(userOrder -> userOrder.getDateTime().equals(dateTimeValue))) {
                return Optional.of("User already has service on this time");
            }

            UserOrder newOrder = new UserOrder(serviceValue, dateTimeValue);

            List<UserOrder> newUserOrders = Lists.newArrayList(userContext.getOrders());
            newUserOrders.add(newOrder);

            userContextProvider.updateContext(userContext.withOrders(newUserOrders));
            String notificationMessage = "Don't miss your order :" + newOrder.toString();
            notifyService.notifyUser(roomId, userId, notificationMessage);
        }

        return Optional.empty();
    }

}
