package com.example.jerryservice.service.Impl;

import com.example.jerryservice.dto.request.MatchCreateRequest;
import com.example.jerryservice.dto.response.LocationResponse;
import com.example.jerryservice.dto.response.MatchCreateResponse;
import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.dto.response.MatchResponse;
import com.example.jerryservice.entity.LocationEntity;
import com.example.jerryservice.entity.MatchEntity;
import com.example.jerryservice.entity.PlayerEntity;
import com.example.jerryservice.entity.UserEntity;
import com.example.jerryservice.mapper.Mapper;
import com.example.jerryservice.repository.LocationRepository;
import com.example.jerryservice.repository.MatchRepository;
import com.example.jerryservice.repository.PlayerRepository;
import com.example.jerryservice.repository.UserRepository;
import com.example.jerryservice.service.MatchService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Override
    @Transactional
    public MatchCreateResponse matchCreate(MatchCreateRequest request) {
        MatchEntity m = new MatchEntity();
        m.setOpponentName(request.getOpponentName());
        m.setMatchDate(request.getMatchDate());
        m.setKickOffTime(request.getTime());
        m.setMaxPlayers(Integer.valueOf(request.getNumberPlayer()));
        m.setLocation(request.getLocation());
        m.setNotes(request.getNotes());
        m.setCreatedAt(LocalDate.now());

        matchRepository.save(m);

        return MatchCreateResponse.builder()
                .createdAt(LocalDate.now())
                .opponentName(m.getOpponentName())
                .matchDate(m.getMatchDate())
                .build();
    }

    @Override
    public void matchDelete(Long id) {

        if (!matchRepository.existsById(id)) {
            throw new EntityNotFoundException("Match not found with id " + id);
        }
        matchRepository.deleteById(id);

    }

    @Override
    @Transactional
    public List<MatchResponse> getAll() {
        List<MatchEntity> all = matchRepository.findAll();
        return all.stream()
                .map(match -> {
                    MatchResponse resp = mapper.toResponse(match);
                    // players is List<PlayerEntity>, map to their user display names
                    resp.setPlayerNames(
                            match.getPlayers().stream()
                                    .map(p -> p.getUser().getDisplayName())
                                    .toList()
                    );
                    return resp;
                })
                .toList();
    }

    @Override
    @Transactional
    public MatchDetailResponse getById(Long id) {
        Optional<MatchEntity> match = matchRepository.findById(id);
        return mapper.toMatchDetailResponse(match);
    }

    @Override
    @Transactional
    public MatchDetailResponse join(Long matchId, String username) {
        if (matchId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "matchId is required");
        }
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username missing");
        }

        UserEntity user = userRepository.findByusername(username);

        MatchEntity match = matchRepository.findByIdWithPlayers(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        boolean alreadyJoined = match.getPlayers().stream()
                .anyMatch(p -> Objects.equals(p.getUser().getId(), user.getId()));
        if (alreadyJoined) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "player already join");
        }

        PlayerEntity player = new PlayerEntity();
        player.setUser(user);
        player.setMatch(match);
        player.setAttributes(50, 50, 50, 50, 50, 50); // optional defaults
        match.addPlayer(player);

        matchRepository.saveAndFlush(match);

        MatchEntity refreshed = matchRepository.findByIdWithPlayers(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
        return mapper.toMatchDetailResponse(Optional.ofNullable(refreshed));
    }

    @Override
    @Transactional
    public MatchDetailResponse leave(Long matchId, String username) {
        if (matchId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "matchId is required");
        }
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username missing");
        }

        UserEntity user = userRepository.findByusername(username);

        MatchEntity match = matchRepository.findByIdWithPlayers(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        PlayerEntity toRemove = match.getPlayers().stream()
                .filter(p -> Objects.equals(p.getUser().getId(), user.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "player not in match"));

        match.removePlayer(toRemove);
        matchRepository.saveAndFlush(match);

        MatchEntity refreshed = matchRepository.findByIdWithPlayers(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
        return mapper.toMatchDetailResponse(Optional.ofNullable(refreshed));
    }

    @Override
    public void locationCreate(String locationName) {
        LocationEntity byname = locationRepository.findByname(locationName);
        if (byname != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "location already exists");
        }
        LocationEntity newLocation = new LocationEntity();
        newLocation.setName(locationName);
        locationRepository.save(newLocation);
    }

    @Override
    public List<LocationResponse> getAllLocation() {
        List<LocationEntity> entities = locationRepository.findAll();
        return mapper.toLocationResponseList(entities);
    }
}

