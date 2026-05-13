package com.aguardientes.azarcafetero.lobby.domain.port.out;

import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import java.util.Optional;

public interface BalanceRepository {
    Optional<Balance> findByUserId(String userId);
    void save(Balance balance);
}