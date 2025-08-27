package com.example.jerryservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "players")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerEntity extends AuditEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchEntity match;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 20, nullable = false)
    private Position position = Position.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 20, nullable = false)
    private PlayerStatus status = PlayerStatus.CONFIRMED; // or WAITLIST

    /** Auto-calculated from 6 attributes */
    @Min(1) @Max(99) @Column(nullable = false)
    private int overall = 50;

    @Min(1) @Max(99) private int pace;       // PAC
    @Min(1) @Max(99) private int shooting;   // SHO
    @Min(1) @Max(99) private int passing;    // PAS
    @Min(1) @Max(99) private int dribbling;  // DRI
    @Min(1) @Max(99) private int defending;  // DEF
    @Min(1) @Max(99) private int physical;   // PHY

    public PlayerEntity() {}

    public enum Position { GK, LB, CB, RB, LWB, RWB, CDM, CM, CAM, LM, RM, LW, RW, ST, CF, UNKNOWN }
    public enum PlayerStatus { CONFIRMED, WAITLIST }

    /** Recompute overall = rounded average of the 6 attributes, clamped to [1..99] */
    public void recomputeOverall() {
        int sum = pace + shooting + passing + dribbling + defending + physical;
        int avg = (int) Math.round(sum / 6.0);
        this.overall = Math.max(1, Math.min(99, avg));
    }

    @PrePersist
    @PreUpdate
    private void beforeSave() { recomputeOverall(); }

    public void setAttributes(int pace, int shooting, int passing, int dribbling, int defending, int physical) {
        this.pace = pace;
        this.shooting = shooting;
        this.passing = passing;
        this.dribbling = dribbling;
        this.defending = defending;
        this.physical = physical;
        recomputeOverall();
    }
}
