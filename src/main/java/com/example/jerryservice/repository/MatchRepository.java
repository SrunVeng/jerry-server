package com.example.jerryservice.repository;

import com.example.jerryservice.entity.MatchEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MatchRepository extends JpaRepository<MatchEntity,Long> {


    @EntityGraph(attributePaths = {"players", "players.user"})
    @Query("select m from MatchEntity m where m.id = :id")
    Optional<MatchEntity> findByIdWithPlayers(@Param("id") Long id);




}
