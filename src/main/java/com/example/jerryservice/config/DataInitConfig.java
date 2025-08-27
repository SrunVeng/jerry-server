package com.example.jerryservice.config;

import com.example.jerryservice.entity.PlayerEntity;
import com.example.jerryservice.entity.RoleEntity;
import com.example.jerryservice.entity.UserEntity;
import com.example.jerryservice.repository.RoleRepository;
import com.example.jerryservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        // 1) Ensure roles


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

        RoleEntity guestRole = roleRepository.findByName("GUEST")
                .orElseGet(() -> {
                    RoleEntity r = new RoleEntity();
                    r.setName("GUEST");
                    return roleRepository.save(r);
                });


        // 2) Seed admin if missing
        final String adminUsername = "srunveng";
        UserEntity existing = userRepository.findByusername(adminUsername);
        if (existing != null) return;

        // create admin user
        UserEntity admin = new UserEntity();
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setDisplayName("Srun(Admin)");
        admin.setEmail("admin@local");                  // must be unique
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode("123")); // change in prod
        admin.setPhoneNumber("000000000");

        // flags (defaults are fine, set explicitly if you want)
        admin.setIsVerified(true);
        admin.setIsBlock(false);
        admin.setIsAccountNonExpired(true);
        admin.setIsAccountNonLocked(true);
        admin.setIsCredentialsNonExpired(true);
        admin.setIsDeleted(false);

        // match your enum definition (TELEGRAM/GUEST or USER/GUEST) — pick the right one
        admin.setSource(UserEntity.SourceType.USER);

        // roles
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(adminRole);
        roles.add(userRole);
        admin.setRoles(roles);

        // 3) Create player and link BOTH SIDES (owning side is Player.user)
        PlayerEntity player = new PlayerEntity();
        player.setPosition(PlayerEntity.Position.ST);
        player.setAttributes(80, 85, 50, 70, 85, 88); // pace, shooting, passing, dribbling, defending, physical

        player.setUser(admin);   // <<< REQUIRED: owning side

        // 4) Save user (will cascade player because of cascade on User.player)
        userRepository.save(admin);
    }
}
