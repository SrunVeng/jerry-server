package com.example.jerryservice.mapper;


import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.dto.response.MatchResponse;
import com.example.jerryservice.entity.MatchEntity;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

@org.mapstruct.Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

//    List<MatchResponse> toMatchDetailResponseList(List<MatchEntity> entity);


    MatchResponse toResponse(MatchEntity entity);

    MatchDetailResponse toMatchDetailResponse(Optional<MatchEntity> byId);
}
