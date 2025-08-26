package com.example.jerryservice.repository;

import com.example.jerryservice.entity.LocationEntity;
import com.example.jerryservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LocationRepository extends JpaRepository<LocationEntity,String> {

    LocationEntity findByName(String name);

}
