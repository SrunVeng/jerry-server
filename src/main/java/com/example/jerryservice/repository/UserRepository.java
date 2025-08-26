package com.example.jerryservice.repository;

import com.example.jerryservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {

    UserEntity findByUsername(String id);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
