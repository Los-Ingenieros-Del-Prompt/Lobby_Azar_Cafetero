package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerIdentityUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;

public class GetPlayerIdentityService implements GetPlayerIdentityUseCase {

    private final PlayerRepository playerRepository;

    public GetPlayerIdentityService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public PlayerIdentityDTO execute(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + username));
        return new PlayerIdentityDTO(player.getUsername(), player.getAvatar(), player.getBalance());
    }
}
