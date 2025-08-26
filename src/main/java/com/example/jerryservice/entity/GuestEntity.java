package com.example.jerryservice.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name="guest")
@Data
public class GuestEntity {

    @Id
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(nullable=false,length=100)
    private String displayName;

    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20)
    private SourceType source;

    public enum SourceType { USER, GUEST }

}
