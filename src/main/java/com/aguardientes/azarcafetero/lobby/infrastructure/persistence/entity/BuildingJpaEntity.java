package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "buildings")
@Getter
@Setter
public class BuildingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("floorOrder ASC")
    private List<FloorJpaEntity> floors = new ArrayList<>();

    public void addFloor(FloorJpaEntity floor) {
        floors.add(floor);
        floor.setBuilding(this);
    }
}
