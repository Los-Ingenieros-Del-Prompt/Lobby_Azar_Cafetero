package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.adapter;

import com.aguardientes.azarcafetero.lobby.domain.model.Table;
import com.aguardientes.azarcafetero.lobby.domain.model.TableState;
import com.aguardientes.azarcafetero.lobby.domain.port.out.TableRepository;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository.TableJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TableRepositoryAdapter implements TableRepository {

    private final TableJpaRepository tableJpaRepository;

    public TableRepositoryAdapter(TableJpaRepository tableJpaRepository) {
        this.tableJpaRepository = tableJpaRepository;
    }

    @Override
    public List<Table> findByFloorIdAndState(UUID floorId, TableState state) {
        return tableJpaRepository.findByFloorIdAndState(floorId, state)
            .stream()
            .map(entity -> Table.create(
                entity.getId(),
                entity.getFloor().getFloorId(),
                entity.getName(),
                entity.getMinBet(),
                entity.getState(),
                entity.getPlayerCount()
            ))
            .toList();
    }
}
