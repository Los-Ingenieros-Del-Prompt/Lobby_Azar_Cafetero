package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Amount;
import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository;
import com.aguardientes.azarcafetero.lobby.domain.port.out.WalletTransactionRepository;





public class ReceiveWinService {
    private final BalanceRepository balanceRepository;
    private final WalletTransactionRepository transactionRepository;
    public ReceiveWinService(BalanceRepository b, WalletTransactionRepository t) {
        this.balanceRepository = b;
        this.transactionRepository = t;
    }
    public WalletTransaction execute(String userId, Amount winAmount) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> { Balance b = new Balance(userId); balanceRepository.save(b); return b; });
        WalletTransaction tx = balance.receiveWin(winAmount);
        balanceRepository.save(balance);
        transactionRepository.save(tx);
        return tx;
    }
}
