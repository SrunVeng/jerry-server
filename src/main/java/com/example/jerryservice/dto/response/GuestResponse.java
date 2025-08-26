package com.example.jerryservice.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestResponse {

    private String guestId;
    private String displayName;
}
