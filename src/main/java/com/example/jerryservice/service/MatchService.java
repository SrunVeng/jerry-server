package com.example.jerryservice.service;

import com.example.jerryservice.dto.request.*;
import com.example.jerryservice.dto.response.*;

import java.util.List;


public interface MatchService {
    MatchCreateResponse matchCreate(MatchCreateRequest request);

    void matchDelete(Long id);

    List<MatchResponse> getAll();

    MatchDetailResponse getById(Long id);

    MatchDetailResponse join(Long matchId, String username);

    MatchDetailResponse leave(Long matchId, String username);

    void locationCreate(String locationName);

    List<LocationResponse> getAllLocation();
}
