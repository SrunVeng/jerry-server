package com.example.jerryservice.dto.request;

import lombok.Data;


@Data
public class MatchUpdateRequest {

    private String id;
    private String opponentName;
    private String matchDate;
    private String time;
    private String pitchNumber;
    private String location;
    private String numberPlayer;
    private String notes;
}
