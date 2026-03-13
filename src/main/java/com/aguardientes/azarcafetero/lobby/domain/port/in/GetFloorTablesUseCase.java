package com.aguardientes.azarcafetero.lobby.domain.port.in;

import com.aguardientes.azarcafetero.lobby.domain.model.TableDTO;

import java.util.List;
import java.util.UUID;

public interface GetFloorTablesUseCase {
    List<TableDTO> execute(UUID floorId);
}
