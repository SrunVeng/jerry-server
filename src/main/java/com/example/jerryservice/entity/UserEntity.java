package com.example.jerryservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name="app_users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String firstName;

    private String lastName;
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;


    private String password;

    private String phoneNumber;

    @Column(length=100)
    private String displayName;

    //System Generate
    private LocalDate createdAt;
    private Boolean isVerified;
    private Boolean isBlock;

    //Security
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;
    private Boolean isDeleted;

    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20)
    private SourceType source;                     // TELEGRAM or GUEST

    // Push messaging
    private String chatId;                           // set when user talks to bot
    private Boolean allowNotification;             // user consent (default false)


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @Column(nullable = false)
    @JsonIgnore
    private List<RoleEntity> roles;

    public enum SourceType { USER, GUEST }

}
