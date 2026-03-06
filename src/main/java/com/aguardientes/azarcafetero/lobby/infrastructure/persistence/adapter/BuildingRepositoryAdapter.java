package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.adapter;

import com.aguardientes.azarcafetero.lobby.domain.model.Building;
import com.aguardientes.azarcafetero.lobby.domain.model.Floor;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BuildingRepository;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.BuildingJpaEntity;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.FloorJpaEntity;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository.BuildingJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BuildingRepositoryAdapter implements BuildingRepository {

    private final BuildingJpaRepository buildingJpaRepository;

    public BuildingRepositoryAdapter(BuildingJpaRepository buildingJpaRepository) {
        this.buildingJpaRepository = buildingJpaRepository;
    }

    @Override
    public Optional<Building> findById(UUID id) {
        return buildingJpaRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public void save(Building building) {
        BuildingJpaEntity entity = toEntity(building);
        buildingJpaRepository.save(entity);
    }

    private Building toDomain(BuildingJpaEntity entity) {
        List<Floor> floors = entity.getFloors().stream()
            .map(this::floorToDomain)
            .collect(Collectors.toList());
        return new Building(entity.getId(), floors);
    }

    private Floor floorToDomain(FloorJpaEntity entity) {
        return new Floor(
            entity.getFloorId(),
            entity.getName(),
            entity.getIcon(),
            entity.getRoute()
        );
    }

    private BuildingJpaEntity toEntity(Building building) {
        BuildingJpaEntity entity = new BuildingJpaEntity();
        entity.setId(building.getId());

        List<Floor> floors = building.getFloors();
        for (int i = 0; i < floors.size(); i++) {
            FloorJpaEntity floorEntity = floorToEntity(floors.get(i));
            floorEntity.setFloorOrder(i + 1);
            entity.addFloor(floorEntity);
        }

        return entity;
    }

    private FloorJpaEntity floorToEntity(Floor floor) {
        FloorJpaEntity entity = new FloorJpaEntity();
        entity.setFloorId(floor.getFloorId());
        entity.setName(floor.getName());
        entity.setIcon(floor.getIcon());
        entity.setRoute(floor.getRoute());
        return entity;
    }
}
