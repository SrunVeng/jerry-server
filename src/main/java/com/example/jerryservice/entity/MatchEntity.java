package com.example.jerryservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
@Data
@EqualsAndHashCode(callSuper = true)
public class MatchEntity extends AuditEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 120)
    private String opponentName;

    @Column(name = "match_date")
    private String matchDate;   // consider LocalDate if possible

    private String kickOffTime; // consider LocalTime if possible

    private String pitchNumber;

    @Size(max = 200)
    private String location;

    @Min(1) @Max(50)
    private Integer maxPlayers = 12;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MatchStatus status = MatchStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MatchResult result;

    @Min(0) @Max(99)
    private Integer teamScore;

    @Min(0) @Max(99)
    private Integer opponentScore;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerEntity> players = new ArrayList<>();

    public void addPlayer(PlayerEntity p) {
        if (p == null) return;
        players.add(p);
        p.setMatch(this);
    }

    public void removePlayer(PlayerEntity p) {
        if (p == null) return;
        players.remove(p);
        p.setMatch(null);
    }

    public enum MatchStatus { SCHEDULED, COMPLETED, CANCELED }
    public enum MatchResult { WIN, LOSS, DRAW }
}
