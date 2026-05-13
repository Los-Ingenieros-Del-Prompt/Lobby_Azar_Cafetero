package com.aguardientes.azarcafetero.lobby.infrastructure.web;
 
import java.math.BigDecimal;
 
public record GameTransactionRequest(String userId, BigDecimal amount) {
    public GameTransactionRequest {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("El userId no puede estar vacío");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
    }
}
