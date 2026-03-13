package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Table;
import com.aguardientes.azarcafetero.lobby.domain.model.TableDTO;
import com.aguardientes.azarcafetero.lobby.domain.model.TableState;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetFloorTablesUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.TableRepository;

import java.util.List;
import java.util.UUID;

public class GetFloorTablesService implements GetFloorTablesUseCase {

    private final TableRepository tableRepository;

    public GetFloorTablesService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public List<TableDTO> execute(UUID floorId) {
        List<Table> tables = tableRepository.findByFloorIdAndState(floorId, TableState.ACTIVE);
        return tables.stream()
            .map(table -> new TableDTO(
                table.getTableId(),
                table.getName(),
                table.getMinBet(),
                table.getState(),
                table.getPlayerCount()
            ))
            .toList();
    }
}
