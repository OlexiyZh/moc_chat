package com.mochallenge.chat.bot;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.mochallenge.chat.service.message.event.ChatEvent;
import com.mochallenge.chat.service.message.event.MessageSentEvent;

import lombok.SneakyThrows;

@Component
public class DialogflowBot implements ChatBot {

    private final static String BOT_ALIAS = "@bot";
    private final static String BOT_ALIAS_REPLACEMENT_REGEX = BOT_ALIAS + " ?";

    @Value("${chat.bot.dialogflow.clientEmail}")
    private String clientEmail;

    @Value("${chat.bot.dialogflow.privateKey}")
    private String privateKey;

    @Value("${chat.bot.dialogflow.projectId}")
    private String projectId;

    private String languageCode = "en-US";

    private SessionsSettings sessionsSettings;

    @SneakyThrows
    @PostConstruct
    private void init() {
        // Workaround. Spring can't inject value with new line character
        this.privateKey = this.privateKey.replace("\\n", "\n");
        Credentials credentials = ServiceAccountCredentials.fromPkcs8(null,
                this.clientEmail,
                this.privateKey,
                null,
                null);

        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
        this.sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
    }

    @Override
    public Optional<ChatEvent> processEvent(ChatEvent event) {
        if (!shouldRespond(event)) {
            return Optional.empty();
        }

        String agentResponse = getAgentResponse(event);
        return StringUtils.isBlank(agentResponse) ?
                Optional.empty() :
                Optional.of(new MessageSentEvent(BOT_ALIAS, event.getRoomId(), agentResponse));
    }

    @SneakyThrows
    private String getAgentResponse(ChatEvent event) {
        String fulfillmentText;
        try (SessionsClient sessionsClient = SessionsClient.create(this.sessionsSettings)) {
            // TODO: Generate sessionId based on roomId
            String sessionId = UUID.randomUUID().toString();
            SessionName session = SessionName.of(projectId, sessionId);

            String queryInputMessage = replaceBotNameFromMessage(event.getMessage());
            TextInput textInput = TextInput.newBuilder()
                    .setText(queryInputMessage)
                    .setLanguageCode(languageCode)
                    .build();

            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            QueryResult queryResult = response.getQueryResult();
            fulfillmentText = queryResult.getFulfillmentText();
        }

        return fulfillmentText;
    }

    private String replaceBotNameFromMessage(String message) {
        return message.replaceFirst(BOT_ALIAS_REPLACEMENT_REGEX, "");
    }

    private boolean shouldRespond(ChatEvent event) {
        return StringUtils.startsWith(event.getMessage(), BOT_ALIAS);
    }

}
