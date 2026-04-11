package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerIdentityUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
import com.aguardientes.azarcafetero.lobby.infrastructure.client.WalletClient;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.UUID;

public class GetPlayerIdentityService implements GetPlayerIdentityUseCase {

    private final PlayerRepository playerRepository;
    private final WalletClient walletClient;

    public GetPlayerIdentityService(PlayerRepository playerRepository, WalletClient walletClient) {
        this.playerRepository = playerRepository;
        this.walletClient = walletClient;
    }

    @Override
    public PlayerIdentityDTO execute(String username, String name, String avatarUrl, String jwtToken) {
        Player player = playerRepository.findByUsername(username)
                .orElseGet(() -> findOrCreate(username, name, avatarUrl));

        BigDecimal balance;
        try {
            balance = walletClient.getBalance(jwtToken);
        } catch (Exception e) {
            balance = player.getBalance(); // fallback al saldo del lobby
        }

        return new PlayerIdentityDTO(player.getDisplayName(), player.getAvatar(), balance);
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
        Player newPlayer = Player.from(UUID.randomUUID(), username, displayName, avatarUrl, new BigDecimal("500"));
        playerRepository.saveNew(newPlayer);
        return newPlayer;
    }
}