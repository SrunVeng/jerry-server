package com.example.jerryservice.mapper;


import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.entity.MatchEntity;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@org.mapstruct.Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

    List<MatchDetailResponse> toMatchDetailResponseList(List<MatchEntity> entity);

}
