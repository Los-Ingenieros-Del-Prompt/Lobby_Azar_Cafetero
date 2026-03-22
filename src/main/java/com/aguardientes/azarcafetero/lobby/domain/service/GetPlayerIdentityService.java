package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerIdentityUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.UUID;

public class GetPlayerIdentityService implements GetPlayerIdentityUseCase {

    private final PlayerRepository playerRepository;

    public GetPlayerIdentityService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public PlayerIdentityDTO execute(String username, String name, String avatarUrl) {
        Player player = playerRepository.findByUsername(username)
                .orElseGet(() -> findOrCreate(username, name, avatarUrl));
        return new PlayerIdentityDTO(player.getDisplayName(), player.getAvatar(), player.getBalance());
    }

    private Player findOrCreate(String username, String name, String avatarUrl) {
        try {
            return registerNewPlayer(username, name, avatarUrl);
        } catch (DataIntegrityViolationException e) {
            // Concurrent request already created the player — just fetch it
            return playerRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("Player creation failed for: " + username));
        }
    }

    private Player registerNewPlayer(String username, String name, String avatarUrl) {
        String displayName = (name != null && !name.isBlank()) ? name : username;
        Player newPlayer = Player.from(UUID.randomUUID(), username, displayName, avatarUrl, new BigDecimal("500"));
        playerRepository.saveNew(newPlayer);
        return newPlayer;
    }
}
