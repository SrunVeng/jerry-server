package com.example.jerryservice.service.Impl;

import com.example.jerryservice.dto.request.*;
import com.example.jerryservice.dto.response.UserLoginResponse;
import com.example.jerryservice.dto.response.UserRegisterResponse;
import com.example.jerryservice.dto.response.GuestResponse;
import com.example.jerryservice.entity.RoleEntity;
import com.example.jerryservice.entity.UserEntity;
import com.example.jerryservice.mapper.Mapper;
import com.example.jerryservice.repository.GuestRepository;
import com.example.jerryservice.repository.RoleRepository;
import com.example.jerryservice.repository.UserRepository;
import com.example.jerryservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final JwtEncoder jwtEncoderAccessToken;
    private final JwtEncoder jwtEncoderRefreshToken;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final RoleRepository roleRepository;

    @Override
    public UserRegisterResponse userRegister(UserRegisterRequest req) {
        UserEntity byUsername = userRepository.findByusername(req.getUsername());

        if (byUsername != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserName Already Exist");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setDisplayName(req.getDisplayName());
        user.setEmail(req.getEmail());  // adjust if you have unique constraint
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword())); // change in prod
        user.setPhoneNumber(req.getPhoneNumber());

        // housekeeping
        user.setCreatedAt(LocalDate.now());
        user.setIsVerified(false);
        user.setIsBlock(false);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsDeleted(false);
        user.setSource(UserEntity.SourceType.USER); // fits your enum {USER, GUEST}

        // roles
        List<RoleEntity> roles = new ArrayList<>();
        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    RoleEntity r = new RoleEntity();
                    r.setName("USER");
                    return roleRepository.save(r);
                });
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return UserRegisterResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Override
    public GuestResponse loginAsGuest(GuestAuthRequest req) {
        if (req == null || req.getUuid() == null || req.getUuid().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "uuid is required");
        }

        // Build a stable username for guests (unique per device/session uuid)
        final String username = "guest_" + req.getUuid().trim();

        // Find or create the USER role
        RoleEntity userRole = roleRepository.findByName("GUEST")
                .orElseGet(() -> {
                    RoleEntity r = new RoleEntity();
                    r.setName("GUEST");
                    return roleRepository.save(r);
                });

        // Upsert the guest user
        UserEntity user = userRepository.findByusername(username);
        if (user == null) {
            user = new UserEntity();
            user.setUsername(username);
            user.setDisplayName(req.getDisplayName());
            // Minimal fields to satisfy non-null constraints
            user.setPassword(passwordEncoder.encode("guest::" + req.getUuid()));
            user.setCreatedAt(LocalDate.now());
            user.setIsVerified(false);
            user.setIsBlock(false);
            user.setIsAccountNonExpired(true);
            user.setIsAccountNonLocked(true);
            user.setIsCredentialsNonExpired(true);
            user.setIsDeleted(false);
            user.setSource(UserEntity.SourceType.GUEST); // IMPORTANT: match your enum/DB

            List<RoleEntity> roles = new ArrayList<>();
            roles.add(userRole);
            user.setRoles(roles);
        } else {
            // Update display name if provided
            if (req.getDisplayName() != null && !req.getDisplayName().isBlank()) {
                user.setDisplayName(req.getDisplayName().trim());
            }
            // Ensure USER role is present
            if (user.getRoles() == null || user.getRoles().stream().noneMatch(r -> "USER".equals(r.getName()))) {
                List<RoleEntity> roles = (user.getRoles() == null) ? new ArrayList<>() : new ArrayList<>(user.getRoles());
                roles.add(userRole);
                user.setRoles(roles);
            }
            // If this account was created with the wrong source before, fix it
            if (user.getSource() != UserEntity.SourceType.GUEST) {
                user.setSource(UserEntity.SourceType.GUEST);
            }
        }

        userRepository.save(user);

        return GuestResponse.builder()
                .guestId(username)                     // client can store & reuse this
                .displayName(user.getDisplayName())
                .build();
    }

    @Override
    public UserLoginResponse userLogin(UserLoginRequest request) {

        //1. Authenticate
        Authentication auth = new UsernamePasswordAuthenticationToken(request.getUsername()
                , request.getPassword());
        auth = daoAuthenticationProvider.authenticate(auth);

        String scope = auth.
                getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        JwtClaimsSet accessJwtClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject(auth.getName())
                .issuer(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .audience(List.of("REACT JS"))
                .claim("scope", scope)
                .build();

        JwtClaimsSet refreshJwtClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject(auth.getName())
                .issuer(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.DAYS))
                .audience(List.of("REACT JS"))
                .claim("scope", scope)
                .build();

        //2. Generate Token
        String accessToken = jwtEncoderAccessToken.encode(JwtEncoderParameters.from(accessJwtClaimsSet)).getTokenValue();
        String refreshToken = jwtEncoderRefreshToken.encode(JwtEncoderParameters.from(refreshJwtClaimsSet)).getTokenValue();


        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("AccessToken")
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public UserLoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        Authentication auth = new BearerTokenAuthenticationToken(refreshTokenRequest.refreshToken());

        auth = jwtAuthenticationProvider.authenticate(auth);


        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();

        Jwt jwt = (Jwt) auth.getPrincipal();

        // Create access token claims set
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(jwt.getId())
                .issuedAt(now)
                .subject(auth.getName())
                .issuer("web")
                .audience(List.of("nextjs", "reactjs"))
                .subject("Access Token")
                .expiresAt(now.plus(1, ChronoUnit.MINUTES))
                .claim("scope", scope)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);
        Jwt encodedJwt = jwtEncoderAccessToken.encode(jwtEncoderParameters);

        String accessToken = encodedJwt.getTokenValue();
        String refreshToken = refreshTokenRequest.refreshToken();

        if (Duration.between(Instant.now(), jwt.getExpiresAt()).toDays() < 2) {
            // Create refresh token claims set
            JwtClaimsSet jwtClaimsSetRefreshToken = JwtClaimsSet.builder()
                    .id(auth.getName())
                    .issuedAt(now)
                    .subject(auth.getName())
                    .issuer("web")
                    .audience(List.of("nextjs", "reactjs"))
                    .subject("Refresh Token")
                    .expiresAt(now.plus(7, ChronoUnit.DAYS))
                    .build();
            JwtEncoderParameters jwtEncoderParametersRefreshToken = JwtEncoderParameters.from(jwtClaimsSetRefreshToken);
            Jwt jwtRefreshToken = jwtEncoderRefreshToken.encode(jwtEncoderParametersRefreshToken);
            refreshToken = jwtRefreshToken.getTokenValue();
        }
        return UserLoginResponse.builder()
                .tokenType("Refresh Token")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public List<UserRegisterResponse> getAll() {
        List<UserEntity> all = userRepository.findAll();
        return mapper.toUserRegisterResponseList(all);
    }

    @Override
    public void delete(Long id) {
        Optional<UserEntity> byId = userRepository.findById(id);
        // role admin cant delete
        if(byId.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Not Found");
        }
        UserEntity user = byId.get();
        if(user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't Delete Admin User");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void update(UserUpdateRequest req) {

        Optional<UserEntity> byId = userRepository.findById(Long.valueOf(req.getId()));
        if(byId.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Not Found");
        }
        UserEntity user = byId.get();
        user.setDisplayName(req.getDisplayName());
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setRoles(req.getRoles());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
    }
}
