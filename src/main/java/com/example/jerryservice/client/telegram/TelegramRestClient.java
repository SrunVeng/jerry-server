package com.example.jerryservice.client.telegram;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramRestClient {


    private final RestTemplate restTemplate;



    @Value("${telegram.bot.token:}")
    private String botToken;

    @Value("${telegram.bot.chat-id:}")
    private String chatId;


    public void sendMessage(String text) {
        if (isBlank(botToken) || isBlank(chatId)) {
            log.warn("Telegram not configured (token/chat-id missing). Skipping send.");
            return;
        }
        try {
            URI url = URI.create("https://api.telegram.org/bot" + botToken + "/sendMessage");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "chat_id", chatId,
                    "text", text,
                    "parse_mode", "HTML",          // or "HTML" / "MarkdownV2"
                    "disable_web_page_preview", true
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(url, request, String.class);

            if (!resp.getStatusCode().is2xxSuccessful()) {
                log.warn("Telegram sendMessage failed: status={} body={}", resp.getStatusCode(), resp.getBody());
            }
        } catch (RestClientException ex) {
            log.error("Error sending Telegram message", ex);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}