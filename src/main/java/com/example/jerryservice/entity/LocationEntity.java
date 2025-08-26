package com.example.jerryservice.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name="location")
@Data
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;





}
