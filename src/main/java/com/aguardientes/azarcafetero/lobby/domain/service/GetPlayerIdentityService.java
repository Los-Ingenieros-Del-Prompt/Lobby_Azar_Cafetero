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
    public PlayerIdentityDTO execute(String username, String name, String avatarUrl, String jwtToken) {
        Player player = playerRepository.findByUsername(username)
                .orElseGet(() -> findOrCreate(username, name, avatarUrl));

        // Balance se sobreescribe en PlayerController con el valor real de BalanceRepository
        return new PlayerIdentityDTO(player.getDisplayName(), player.getAvatar(), BigDecimal.ZERO);
    }

    private Player findOrCreate(String username, String name, String avatarUrl) {
        try {
            return registerNewPlayer(username, name, avatarUrl);
        } catch (DataIntegrityViolationException e) {
            return playerRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("Player creation failed for: " + username));
        }
    }

    private Player registerNewPlayer(String username, String name, String avatarUrl) {
        String displayName = (name != null && !name.isBlank()) ? name : username;
        Player newPlayer = Player.from(UUID.randomUUID(), username, displayName, avatarUrl, BigDecimal.ZERO);
        playerRepository.saveNew(newPlayer);
        return newPlayer;
    }
}