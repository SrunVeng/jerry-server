package com.example.jerryservice.dto.response;

import com.example.jerryservice.entity.PlayerEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class MatchResponse {

    private Long id;
    private String opponentName;
    private String time;
    private String location;
    private String numberPlayer;
    private List<String> playerNames;
    private String status;
    private String result;
    private String opponentScore;
    private String teamScore;
    private String notes;
    private String matchDate;
    private String createdAt;

}
