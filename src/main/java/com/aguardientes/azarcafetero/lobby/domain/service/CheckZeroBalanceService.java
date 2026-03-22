package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.port.in.CheckZeroBalanceUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;

import java.math.BigDecimal;

public class CheckZeroBalanceService implements CheckZeroBalanceUseCase {

    private final PlayerRepository playerRepository;

    public CheckZeroBalanceService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public boolean execute(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + username));
        return BigDecimal.ZERO.compareTo(player.getBalance()) == 0;
    }
}
