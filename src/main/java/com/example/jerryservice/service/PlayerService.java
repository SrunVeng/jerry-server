package com.example.jerryservice.service;

import com.example.jerryservice.dto.request.MatchCreateRequest;
import com.example.jerryservice.dto.response.MatchCreateResponse;
import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.dto.response.MatchResponse;
import com.example.jerryservice.dto.response.PlayerStatsResponse;
import com.example.jerryservice.entity.MatchEntity;

import java.util.List;


public interface PlayerService {
    PlayerStatsResponse getStatsForUser(Long userId);
    List<MatchEntity> getMatchesForUser(Long userId);
}
