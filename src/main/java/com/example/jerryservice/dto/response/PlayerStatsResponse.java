package com.example.jerryservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerStatsResponse {

    private Long userId;
    private long totalJoined;     // how many matches this user joined
    private long confirmed;       // optional: CONFIRMED only
    private long waitlist;        // optional: WAITLIST only
}
