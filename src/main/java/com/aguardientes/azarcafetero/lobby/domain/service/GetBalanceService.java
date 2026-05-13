package com.aguardientes.azarcafetero.lobby.domain.service;


import com.aguardientes.azarcafetero.lobby.domain.model.Balance;

import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository;




public class GetBalanceService {
    private final BalanceRepository balanceRepository;
    public GetBalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }
    public Balance execute(String userId) {
        return balanceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Balance b = new Balance(userId);
                    balanceRepository.save(b);
                    return b;
                });
    }
}



