package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "buildings")
@Getter
@Setter
public class BuildingJpaEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew = true;

    @PostLoad
    @PostPersist
    void markNotNew() { this.isNew = false; }

    @Override
    public boolean isNew() { return isNew; }

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("floorOrder ASC")
    private List<FloorJpaEntity> floors = new ArrayList<>();

    public void addFloor(FloorJpaEntity floor) {
        floors.add(floor);
        floor.setBuilding(this);
    }
}
