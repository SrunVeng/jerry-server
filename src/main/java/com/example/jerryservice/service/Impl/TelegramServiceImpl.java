
package com.example.jerryservice.service.Impl;

import com.example.jerryservice.client.telegram.TelegramRestClient;
import com.example.jerryservice.entity.MatchEntity;
import com.example.jerryservice.entity.UserEntity;
import com.example.jerryservice.service.TelegramService;
import com.example.jerryservice.utils.TelegramTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.example.jerryservice.utils.TelegramTemplates.Lang;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final TelegramRestClient telegramRestClient;

    @Value("${app.frontend.base-url:https://jerry-ui-one.vercel.app/}")
    private String appBaseUrl;

    @Value("${lang.push:EN}")              // <-- read toggle from yml
    private String pushLang;

    private Lang lang() {
        try { return Lang.valueOf(String.valueOf(pushLang).trim().toUpperCase()); }
        catch (Exception ignore) { return Lang.EN; }
    }

    private String inviteUrlForMatch(Long matchId) {
        if (appBaseUrl == null) return "";
        // normalize to remove trailing slashes
        return appBaseUrl.replaceAll("/+$", "");
    }

    @Override
    public void sendMessageSummaryCreate(MatchEntity match) {
        String invite = inviteUrlForMatch(match != null ? match.getId() : null);
        telegramRestClient.sendMessage(
                TelegramTemplates.buildSummaryTemplate(match, invite, lang())
        );
    }

    @Override
    public void sendMessageSummaryUpdate(MatchEntity match) {
        String invite = inviteUrlForMatch(match != null ? match.getId() : null);
        telegramRestClient.sendMessage(
                TelegramTemplates.buildSummaryTemplateUpdate(match, invite, lang())
        );
    }

    @Override
    public void sendMessagePlayerJoined(UserEntity player, MatchEntity match) {
        String invite = inviteUrlForMatch(match != null ? match.getId() : null);
        telegramRestClient.sendMessage(
                TelegramTemplates.buildPlayerJoinedTemplate(player, match, invite, lang())
        );
    }

    @Override
    public void sendMessagePlayerLeft(UserEntity player, MatchEntity match) {
        String invite = inviteUrlForMatch(match != null ? match.getId() : null);
        telegramRestClient.sendMessage(
                TelegramTemplates.buildPlayerLeftTemplate(player, match, invite, lang())
        );
    }

    @Override
    public void sendMessageCreatedMatch(MatchEntity match) {
        String invite = inviteUrlForMatch(match != null ? match.getId() : null);
        telegramRestClient.sendMessage(
                TelegramTemplates.buildMatchCreatedTemplate(match, invite, lang())
        );
    }

    // If you added cancel notification:
    public void sendMessageCancelledMatch(MatchEntity match) {
        telegramRestClient.sendMessage(
                TelegramTemplates.buildMatchCancelledTemplate(match, lang())
        );
    }
}
