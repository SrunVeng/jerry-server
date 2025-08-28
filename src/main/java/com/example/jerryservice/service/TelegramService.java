package com.example.jerryservice.service;

import com.example.jerryservice.entity.MatchEntity;
import com.example.jerryservice.entity.UserEntity;

public interface TelegramService {


    void sendMessageSummaryCreate(MatchEntity match); // Optional (use when you need)

    void sendMessageSummaryUpdate(MatchEntity match); // Optional (use when you need)
    void sendMessagePlayerJoined(UserEntity player, MatchEntity match);
    void sendMessagePlayerLeft(UserEntity player, MatchEntity match);
    void sendMessageCreatedMatch(MatchEntity match);

    void sendMessageCancelledMatch(MatchEntity match);

}
