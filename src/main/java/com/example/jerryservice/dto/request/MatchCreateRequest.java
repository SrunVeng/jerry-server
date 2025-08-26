package com.example.jerryservice.dto.request;

import lombok.Data;



@Data
public class MatchCreateRequest {

    private String opponentName;
    private String matchDate;
    private String time;
    private String location;
    private String numberPlayer;
    private String notes;
}
