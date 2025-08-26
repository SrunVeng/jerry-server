package com.example.jerryservice.dto.response;

import com.example.jerryservice.entity.PlayerEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
public class MatchDetailResponse {

    private String opponentName;
    private String time;
    private String location;
    private String numberPlayer;
    private List<PlayerEntity> players;
    private String status;
    private String result;
    private String opponentScore;
    private String teamScore;
    private String notes;
    private String matchDate;
    private String createdAt;
    private String upComingDate;

}
