package com.aguardientes.azarcafetero.lobby.domain.port.in;

import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;

public interface GetPlayerIdentityUseCase {
    PlayerIdentityDTO execute(String username, String name, String avatarUrl);
}
