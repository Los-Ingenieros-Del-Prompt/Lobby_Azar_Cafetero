package com.aguardientes.azarcafetero.lobby.domain.port.in;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;

import java.util.UUID;

public interface GetPlayerByIdUseCase {
    Player execute(UUID playerId);
}
