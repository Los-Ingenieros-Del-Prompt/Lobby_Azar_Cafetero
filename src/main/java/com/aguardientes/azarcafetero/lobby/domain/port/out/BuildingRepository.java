package com.aguardientes.azarcafetero.lobby.domain.port.out;

import com.aguardientes.azarcafetero.lobby.domain.model.Building;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository {

    Optional<Building> findById(UUID id);

    void save(Building building);
}
