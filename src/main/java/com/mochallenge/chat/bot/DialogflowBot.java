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

    // TODO: Spring can't inject value with new line character. Implement workaround
    //@Value("${chat.bot.dialogflow.privateKey}")
    private String privateKey = "-----BEGIN PRIVATE KEY-----\nMIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQCbEma1QVd+YAyp\n/3X/Hy2b8Z5Q95cSIuVoIQmA+YVtkc+6QKmH6QujGBfNb9XVceIN3evO3vLGKGt8\nwTHa3Uy9ygXSoaf86BTHpTTX7Qg+EqRJkmlLwBLahbYVp4j6eFfHkDJsHqABxoke\nazt2/3bSlKX0mfseD6M9mq6smG3QLI++z34VzQnqolNinQ6aYsm8QPSrpkoMrKc0\nx1yPk+5rSSEVECUy6luHzHK2fwdjjKIZYz2A33/q4TSQRssVbM18zKSY5EwqGNZP\n3e+Sn5FYbOHaAbVeIstKoAcpMqkC2bAUGqbQSm3u9mCVhUI7TXgT+q5L4vgib3KB\nTSizSxYnAgMBAAECgf9Zpkm6gAwVA3JO1WFiHKRBg24DYR/1XDdD+9WCur5omeN9\nyeQ4roILx+H64qy8uRn26lVpQ3sXFEcP2KD55vZ2wlSbcNmtXH6m7Nuf+WjKefyA\njCRTprc/ng2mdzS5mNkmhjagSxQ18pFTBKtcIFSRaV6ICn0i4+d7tfFF1tmlfzb0\nAo08/+Hql4zsZfJ+EYX/+/oVlfzSHz394RL4Ja5Fe1Z4YAu4J6DnW47kSKuzhLAh\nh1c57AcSZpJzyoQaU5B95dsYuP0liwza878CcFAO23Bdv+TTsG1Sg+D9N9VWRIsx\njx8UeCkEcw1cdXbwuKxtwHzu6F7pz2+frvSbz0ECgYEA0T3qcRiLNzaoGQnYagLY\nv9zZOri8Ms1hFicf8ukHfCBBzqBJEbK+Lx6awWPcNWABCFzytdeUu76hhz2mnpvg\n1BBsNwgmSs1/eEgviGfc8MTi2gnO3MR57GDTZZk4u7v44KjCAGQ2SlnFpu9M4vEi\nph1jNp6gMBFdkTyQL48lOrkCgYEAvbmWVvAyC+33sM56fP9LvaVyJRLOSm+Jul69\nyQw4S4PUGzAVwE9kpx7W8p8EcSy7XBKNHpJZ5QxfbZO6LKiFoYWF4XeGgZZ9WeGf\nhl5VGMxTjJ212xAApc/0ER9gSbjdjp8PdOJoevUALCObRKdAPq01BFRcM56YMdCj\nwUjg598CgYBQhHXpzVsNBSBMekhoCu5jblW9TgdFuViJ0MZvkBKMRlFsdKuJfvHG\nag19K7M6atYa5MGVDf8y+9lmlGfAkxVCJYuu5ZjuogNSQtzfrbAMiocNBkcvFwAf\n4OaYKwz02RGdSfNVeTqXA9KWOXGpxsbhi195TjMwLA6Ia/jEBF58KQKBgCx4EH3m\nJMTZDSJa8yjTKeyUl9GKu+GEjTKqvC8gdx10E8YfmimOMlhdiSIrFX51bqDhFESj\nJwDhyohDhPKsuU2nnz6+pj8/h7gGgz6aY7XBRqAB4zsqYAx9Voag5Q/CjeO88oGC\nA902CclOuzj3Pr6yGGggYdATjza14XwPWe/fAoGBAJnmXp6/TwSJRi3tbUUqTMbY\nO0ChD5FtcUff+NkP4/OZmBI/ZL2HDYTmX658Rg/3aYKTGHw6Fxry05ARIh+VGFHE\nMqPW/UFx+zeuWbfPdprjzKiweKILNrQzndpsN4clX8ypm6fMXmU8vwNL1d+PBjEv\nQGZR8IWc1FYuT0sYzsP/\n-----END PRIVATE KEY-----\n";

    @Value("${chat.bot.dialogflow.projectId}")
    private String projectId;

    private String languageCode = "en-US";

    private SessionsSettings sessionsSettings;

    @SneakyThrows
    @PostConstruct
    private void init() {
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
