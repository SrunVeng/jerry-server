package com.example.jerryservice.repository;

import com.example.jerryservice.entity.GuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GuestRepository extends JpaRepository<GuestEntity,String> {
    Optional<GuestEntity> findById(String id);
}
