package com.example.jerryservice.controller;


import com.example.jerryservice.dto.request.MatchCreateRequest;
import com.example.jerryservice.dto.request.UserRegisterRequest;
import com.example.jerryservice.dto.response.MatchCreateResponse;
import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.dto.response.MatchResponse;
import com.example.jerryservice.dto.response.UserRegisterResponse;
import com.example.jerryservice.service.MatchService;
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
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;
    private final UserService userService;


    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER')")
    @PostMapping("/create")
    public ResponseEntity<MatchCreateResponse> create(@Valid @RequestBody MatchCreateRequest request) {
        MatchCreateResponse res = matchService.matchCreate(request);
        return ResponseEntity.ok(res);
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER')")
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.matchDelete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER')")
    @GetMapping("/getAll")
    public ResponseEntity<List<MatchResponse>> getAll() {
        List<MatchResponse> res = matchService.getAll();
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER')")
    @GetMapping("/getMatchDetailsById/{id}")
    public ResponseEntity<MatchDetailResponse> getById(@PathVariable Long id) {
        MatchDetailResponse res = matchService.getById(id);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/join/{matchId}")
    public ResponseEntity<MatchDetailResponse> join(@PathVariable Long matchId,
                                                    Authentication authentication) {
        String username = authentication.getName();
        MatchDetailResponse dto = matchService.join(matchId, username);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/leave/{matchId}")
    public ResponseEntity<MatchDetailResponse> leave(@PathVariable Long matchId,
                                                     Authentication authentication) {
        String username = authentication.getName();
        MatchDetailResponse dto = matchService.leave(matchId, username);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/location/create")
    public ResponseEntity<Void> leave(@RequestParam String locationName) {
         matchService.locationCreate(locationName);
        return ResponseEntity.noContent().build();
    }


}
