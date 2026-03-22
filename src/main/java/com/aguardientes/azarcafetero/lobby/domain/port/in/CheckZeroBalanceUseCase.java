package com.aguardientes.azarcafetero.lobby.domain.port.in;

public interface CheckZeroBalanceUseCase {
    boolean execute(String username);
}
