package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "floors")
@Getter
@Setter
public class FloorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID floorId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private String route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private BuildingJpaEntity building;

    @Column(name = "floor_order")
    private Integer floorOrder;
}
