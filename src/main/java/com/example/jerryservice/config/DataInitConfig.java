package com.example.jerryservice.config;

import com.example.jerryservice.entity.RoleEntity;
import com.example.jerryservice.entity.UserEntity;
import com.example.jerryservice.repository.RoleRepository;
import com.example.jerryservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitConfig {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        // 1) Ensure roles exist (store names WITHOUT "ROLE_" prefix)
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    RoleEntity r = new RoleEntity();
                    r.setName("ADMIN");
                    return roleRepository.save(r);
                });

        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    RoleEntity r = new RoleEntity();
                    r.setName("USER");
                    return roleRepository.save(r);
                });

        // 2) Seed an admin account if missing
        final String adminUsername = "admin";
        UserEntity byUsername = userRepository.findByUsername(adminUsername);
        if (byUsername == null) {
            UserEntity admin = new UserEntity();
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setDisplayName("System Admin");
            admin.setEmail("admin@local");  // adjust if you have unique constraint
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode("123")); // change in prod
            admin.setPhoneNumber("000000000");

            // housekeeping
            admin.setCreatedAt(LocalDate.now());
            admin.setIsVerified(true);
            admin.setIsBlock(false);
            admin.setIsAccountNonExpired(true);
            admin.setIsAccountNonLocked(true);
            admin.setIsCredentialsNonExpired(true);
            admin.setIsDeleted(false);
            admin.setSource(UserEntity.SourceType.USER); // fits your enum {USER, GUEST}

            // roles
            List<RoleEntity> roles = new ArrayList<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }
}
