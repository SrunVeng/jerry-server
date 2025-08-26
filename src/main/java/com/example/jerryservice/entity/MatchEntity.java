package com.example.jerryservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name="matches")
@Data
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String opponentName;
    private String time;

    private String location;
    private String numberPlayer;

    private String status; // SCHEDULED, COMPLETED, CANCELED
    private String result; // WIN, LOSS, DRAW
    private String opponentScore; // e.g., "3-2"
    private String teamScore;
    private String notes;
    private String matchDate;
    private LocalDate createdAt;
    private String upComingDate;


}
