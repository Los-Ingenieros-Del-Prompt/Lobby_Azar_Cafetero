package com.aguardientes.azarcafetero.lobby.domain.port.out;

import com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction;
import java.util.List;

public interface WalletTransactionRepository {
    void save(WalletTransaction transaction);
    List<WalletTransaction> findByUserId(String userId);
}