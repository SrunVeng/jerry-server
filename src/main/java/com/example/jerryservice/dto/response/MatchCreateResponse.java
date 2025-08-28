package com.example.jerryservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;



@Data
@Builder
public class MatchCreateResponse {

    private String opponentName;
    private String pitchNumber;
    private String kickOffTime;
    private String location;
    private String matchDate;
    private LocalDate createdAt;

}
