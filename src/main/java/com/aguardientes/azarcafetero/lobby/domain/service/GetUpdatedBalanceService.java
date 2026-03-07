package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetUpdatedBalanceUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;

import java.math.BigDecimal;

public class GetUpdatedBalanceService implements GetUpdatedBalanceUseCase {

    private final PlayerRepository playerRepository;

    public GetUpdatedBalanceService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public BigDecimal execute(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + username));
        return player.getBalance();
    }
}
