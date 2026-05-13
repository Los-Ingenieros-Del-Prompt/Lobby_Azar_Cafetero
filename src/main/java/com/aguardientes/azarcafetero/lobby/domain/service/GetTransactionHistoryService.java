package com.aguardientes.azarcafetero.lobby.domain.service;


import com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction;
import com.aguardientes.azarcafetero.lobby.domain.port.out.WalletTransactionRepository;

import java.util.List;


public class GetTransactionHistoryService {
    private final WalletTransactionRepository transactionRepository;
    public GetTransactionHistoryService(WalletTransactionRepository t) {
        this.transactionRepository = t;
    }
    public List<WalletTransaction> execute(String userId) {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("El userId no puede estar vacío");
        return transactionRepository.findByUserId(userId);
    }
}