package com.example.jerryservice.mapper;


import com.example.jerryservice.dto.response.LocationResponse;
import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.dto.response.MatchResponse;
import com.example.jerryservice.dto.response.UserRegisterResponse;
import com.example.jerryservice.entity.LocationEntity;
import com.example.jerryservice.entity.MatchEntity;
import com.example.jerryservice.entity.RoleEntity;
import com.example.jerryservice.entity.UserEntity;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Optional;

@org.mapstruct.Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

//    List<MatchResponse> toMatchDetailResponseList(List<MatchEntity> entity);


    MatchResponse toResponse(MatchEntity entity);

    MatchDetailResponse toMatchDetailResponse(Optional<MatchEntity> byId);

    List<LocationResponse> toLocationResponseList(List<LocationEntity> entities);

    List<UserRegisterResponse> toUserRegisterResponseList(List<UserEntity> all);


    @Mapping(target = "role", expression = "java(mapRoles(entity.getRoles()))")
    UserRegisterResponse toUserRegisterResponse(UserEntity entity);


    default String mapRoles(List<RoleEntity> roles) {
        if (roles == null || roles.isEmpty()) return null;
        // if you only want one role (first)
        return roles.get(0).getName();
        // or if you want comma separated:
        // return roles.stream().map(RoleEntity::getName).collect(Collectors.joining(","));
    }
}
