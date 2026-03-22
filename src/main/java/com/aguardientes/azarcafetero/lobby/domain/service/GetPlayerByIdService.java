package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerByIdUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;

import java.util.UUID;

public class GetPlayerByIdService implements GetPlayerByIdUseCase {

    private final PlayerRepository playerRepository;

    public GetPlayerByIdService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player execute(UUID playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
    }
}
