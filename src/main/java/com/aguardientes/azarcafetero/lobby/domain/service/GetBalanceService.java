package com.aguardientes.azarcafetero.lobby.domain.service;


import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;



public class GetBalanceService {
    private final BalanceRepository balanceRepository;
    public GetBalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }
    public Balance execute(String userId) {
        Optional<Balance> existingBalance = balanceRepository.findByUserId(userId);
        if (existingBalance.isPresent()) {
            return existingBalance.get();
        }

        Balance balance = new Balance(userId);
        try {
            balanceRepository.save(balance);
            return balance;
        } catch (DataIntegrityViolationException ex) {
            return balanceRepository.findByUserId(userId)
                    .orElseThrow(() -> ex);
        }
    }
}



