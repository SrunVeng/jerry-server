package com.example.jerryservice.repository;


import com.example.jerryservice.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity,Long> {
    long countByUserId(Long userId);
    List<PlayerEntity> findByUserId(Long userId);
}
