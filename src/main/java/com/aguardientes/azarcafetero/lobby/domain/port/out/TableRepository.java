package com.aguardientes.azarcafetero.lobby.domain.port.out;

import com.aguardientes.azarcafetero.lobby.domain.model.Table;
import com.aguardientes.azarcafetero.lobby.domain.model.TableState;

import java.util.List;
import java.util.UUID;

public interface TableRepository {
    List<Table> findByFloorIdAndState(UUID floorId, TableState state);
}
