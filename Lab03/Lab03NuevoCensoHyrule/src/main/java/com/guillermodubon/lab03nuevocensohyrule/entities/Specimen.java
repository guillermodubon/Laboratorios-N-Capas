package com.guillermodubon.lab03nuevocensohyrule.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Table(name = "Specimen")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Specimen {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "region")
    private String region;

    @Column(name = "dangerLevel")
    private Integer dangerLevel;

    @Column(name = "isFriendly")
    private Boolean isFriendly;
}
