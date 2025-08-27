package com.example.jerryservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Table(name = "app_users")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 60)
    private String firstName;

    @Size(max = 60)
    private String lastName;

    @Email @Size(max = 120)
    private String email;

    @Size(max = 60)
    private String username;

    @JsonIgnore
    @Size(max = 255)
    private String password;

    @Size(max = 30)
    private String phoneNumber;

    @Column(length = 100)
    private String displayName;

    // Security & lifecycle
    private Boolean isVerified = Boolean.FALSE;
    private Boolean isBlock = Boolean.FALSE;
    private Boolean isAccountNonExpired = Boolean.TRUE;
    private Boolean isAccountNonLocked = Boolean.TRUE;
    private Boolean isCredentialsNonExpired = Boolean.TRUE;
    private Boolean isDeleted = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SourceType source = SourceType.GUEST;   // GUEST by default

    // Push messaging
    @Column(name = "chat_id")
    private String chatId;                          // Telegram chat id, etc.
    private Boolean allowNotification = Boolean.FALSE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @JsonIgnore
    private List<RoleEntity> roles;


    public enum SourceType { USER, GUEST }
}
