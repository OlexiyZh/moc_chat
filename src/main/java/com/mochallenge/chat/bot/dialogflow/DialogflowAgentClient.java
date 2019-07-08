package com.mochallenge.chat.bot.dialogflow;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

import lombok.NonNull;
import lombok.SneakyThrows;

@Component
@ConditionalOnBean(DialogflowBot.class)
public class DialogflowAgentClient {

    @Value("${chat.bot.dialogflow.clientEmail}")
    private String clientEmail;

    @Value("${chat.bot.dialogflow.privateKey}")
    private String privateKey;

    @Value("${chat.bot.dialogflow.projectId}")
    private String projectId;

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

    @SneakyThrows
    public String sendTextMessage(@NonNull DialogflowSessionContext context, @NonNull String message) {
        String fulfillmentText;
        try (SessionsClient sessionsClient = SessionsClient.create(this.sessionsSettings)) {
            SessionName session = SessionName.of(projectId, context.getSessionId());
            TextInput textInput = TextInput.newBuilder()
                    .setText(message)
                    .setLanguageCode(context.getLanguageCode())
                    .build();

            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            QueryResult queryResult = response.getQueryResult();
            fulfillmentText = queryResult.getFulfillmentText();
        }

        return fulfillmentText;
    }
}
