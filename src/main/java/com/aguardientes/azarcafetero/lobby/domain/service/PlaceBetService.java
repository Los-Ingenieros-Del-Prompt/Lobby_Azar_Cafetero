package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Amount;
import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository;
import com.aguardientes.azarcafetero.lobby.domain.port.out.WalletTransactionRepository;



class PlaceBetService {
    private final BalanceRepository balanceRepository;
    private final WalletTransactionRepository transactionRepository;
    public PlaceBetService(BalanceRepository b, WalletTransactionRepository t) {
        this.balanceRepository = b;
        this.transactionRepository = t;
    }
    public WalletTransaction execute(String userId, Amount betAmount) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> { Balance b = new Balance(userId); balanceRepository.save(b); return b; });
        if (!balance.hasSufficientFunds(betAmount))
            throw new IllegalStateException("Saldo insuficiente. Actual: "
                    + balance.getAmount().getValue() + ", requerido: " + betAmount.getValue());
        WalletTransaction tx = balance.placeBet(betAmount);
        balanceRepository.save(balance);
        transactionRepository.save(tx);
        return tx;
    }
}
