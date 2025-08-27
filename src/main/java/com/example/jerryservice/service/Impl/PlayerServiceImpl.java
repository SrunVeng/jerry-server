package com.example.jerryservice.service.Impl;


import com.example.jerryservice.dto.response.PlayerStatsResponse;
import com.example.jerryservice.entity.MatchEntity;
import com.example.jerryservice.entity.PlayerEntity;
import com.example.jerryservice.repository.PlayerRepository;
import com.example.jerryservice.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Override
    public PlayerStatsResponse getStatsForUser(Long userId) {
        List<PlayerEntity> rows = playerRepository.findByUserId(userId);
        long total = rows.size();
        long confirmed = rows.stream()
                .filter(p -> p.getStatus() == PlayerEntity.PlayerStatus.CONFIRMED).count();
        long waitlist = rows.stream()
                .filter(p -> p.getStatus() == PlayerEntity.PlayerStatus.WAITLIST).count();

        return PlayerStatsResponse.builder()
                .userId(userId)
                .totalJoined(total)
                .confirmed(confirmed)
                .waitlist(waitlist)
                .build();
    }

    @Override
    public List<MatchEntity> getMatchesForUser(Long userId) {
        return playerRepository.findByUserId(userId).stream()
                .map(PlayerEntity::getMatch)
                .toList();
    }

}
