package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerIdentityUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import com.aguardientes.azarcafetero.lobby.domain.model.Balance; 
import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository; 
import java.math.BigDecimal;
import java.util.UUID;


public class GetPlayerIdentityService implements GetPlayerIdentityUseCase {
    private final PlayerRepository playerRepository;
    private final BalanceRepository balanceRepository; // ← agregar

    public GetPlayerIdentityService(PlayerRepository playerRepository,
                                    BalanceRepository balanceRepository) { // ← agregar
        this.playerRepository = playerRepository;
        this.balanceRepository = balanceRepository; // ← agregar
    }

    @Override
    public PlayerIdentityDTO execute(String username, String name, String avatarUrl, String jwtToken) {
        playerRepository.findByUsername(username)
                .orElseGet(() -> findOrCreate(username, name, avatarUrl));
        return new PlayerIdentityDTO(name, avatarUrl, BigDecimal.ZERO);
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

        // ← Crear balance inicial con 500 fichas solo si no existe
        if (balanceRepository.findByUserId(username).isEmpty()) {
            Balance initialBalance = new Balance(username); // ya tiene INITIAL_BALANCE = 500
            balanceRepository.save(initialBalance);
        }

        return newPlayer;
    }
}