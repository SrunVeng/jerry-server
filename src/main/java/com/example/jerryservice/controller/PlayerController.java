package com.example.jerryservice.controller;


import com.example.jerryservice.dto.request.MatchCreateRequest;
import com.example.jerryservice.dto.response.MatchCreateResponse;
import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.dto.response.MatchResponse;
import com.example.jerryservice.dto.response.PlayerStatsResponse;
import com.example.jerryservice.entity.MatchEntity;
import com.example.jerryservice.service.MatchService;
import com.example.jerryservice.service.PlayerService;
import com.example.jerryservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService playerService;










}
