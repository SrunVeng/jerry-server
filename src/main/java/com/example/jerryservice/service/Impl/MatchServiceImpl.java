package com.example.jerryservice.service.Impl;

import com.example.jerryservice.dto.request.MatchCreateRequest;
import com.example.jerryservice.dto.request.UserRegisterRequest;
import com.example.jerryservice.dto.response.MatchCreateResponse;
import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.dto.response.UserRegisterResponse;
import com.example.jerryservice.entity.MatchEntity;
import com.example.jerryservice.mapper.Mapper;
import com.example.jerryservice.repository.MatchRepository;
import com.example.jerryservice.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final Mapper mapper;


    @Override
    public MatchCreateResponse matchCreate(MatchCreateRequest request) {

        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setOpponentName(request.getOpponentName());
        matchEntity.setMatchDate(request.getMatchDate());
        matchEntity.setTime(request.getTime());
        matchEntity.setNumberPlayer(request.getNumberPlayer());
        matchEntity.setLocation(request.getLocation());
        matchEntity.setNotes(request.getNotes());

        matchEntity.setCreatedAt(LocalDate.now());

        matchRepository.save(matchEntity);
        return MatchCreateResponse.builder()
                .createdAt(LocalDate.now())
                .opponentName(request.getOpponentName())
                .matchDate(request.getMatchDate())
                .build();

    }

    @Override
    public List<MatchDetailResponse> getAll() {

        List<MatchEntity> all = matchRepository.findAll();
       return mapper.toMatchDetailResponseList(all);

    }


}
