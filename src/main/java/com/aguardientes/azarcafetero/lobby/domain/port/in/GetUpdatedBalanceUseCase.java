package com.aguardientes.azarcafetero.lobby.domain.port.in;

import java.math.BigDecimal;

public interface GetUpdatedBalanceUseCase {
    BigDecimal execute(String username);
}
