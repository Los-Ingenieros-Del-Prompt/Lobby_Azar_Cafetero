package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "floors")
@Getter
@Setter
public class FloorJpaEntity implements Persistable<UUID> {

    @Id
    private UUID floorId;

    @Transient
    private boolean isNew = true;

    @PostLoad
    @PostPersist
    void markNotNew() { this.isNew = false; }

    @Override
    public UUID getId() { return floorId; }

    @Override
    public boolean isNew() { return isNew; }

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
