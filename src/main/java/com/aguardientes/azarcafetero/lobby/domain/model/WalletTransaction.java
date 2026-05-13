package com.aguardientes.azarcafetero.lobby.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class WalletTransaction {

    private final String transactionId;
    private final String userId;
    private final Amount amount;
    private final TransactionType type;
    private final LocalDateTime createdAt;
    private final String description;

    public WalletTransaction(String userId, Amount amount,
                             TransactionType type, String description) {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("El userId no puede estar vacío");
        if (amount == null)
            throw new IllegalArgumentException("El monto no puede ser nulo");
        if (type == null)
            throw new IllegalArgumentException("El tipo no puede ser nulo");
        this.transactionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.description = description;
    }

    // Constructor para reconstruir desde persistencia
    public WalletTransaction(String transactionId, String userId, Amount amount,
                             TransactionType type, LocalDateTime createdAt, String description) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
        this.description = description;
    }

    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
    public Amount getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getDescription() { return description; }
}