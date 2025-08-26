package com.example.jerryservice.repository;

import com.example.jerryservice.entity.LocationEntity;
import com.example.jerryservice.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MatchRepository extends JpaRepository<MatchEntity,String> {

    MatchEntity findById(Long id);

}
