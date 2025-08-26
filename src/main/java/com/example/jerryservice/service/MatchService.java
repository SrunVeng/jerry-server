package com.example.jerryservice.service;

import com.example.jerryservice.dto.request.*;
import com.example.jerryservice.dto.response.*;

import java.util.List;


public interface MatchService {
    MatchCreateResponse matchCreate(MatchCreateRequest request);

    List<MatchDetailResponse> getAll();
}
