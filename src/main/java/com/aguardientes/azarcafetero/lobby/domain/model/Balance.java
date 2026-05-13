package com.aguardientes.azarcafetero.lobby.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Balance {

    private static final Amount DAILY_BONUS = Amount.of(100);
    private static final Amount INITIAL_BALANCE = Amount.of(500);

    private final String userId;
    private Amount amount;
    private LocalDate lastBonusDate;

    public Balance(String userId) {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("El userId no puede estar vacío");
        this.userId = userId;
        this.amount = INITIAL_BALANCE;
        this.lastBonusDate = null;
    }

    // Constructor para reconstruir desde persistencia
    public Balance(String userId, Amount amount, LocalDate lastBonusDate) {
        this.userId = userId;
        this.amount = amount;
        this.lastBonusDate = lastBonusDate;
    }

    public WalletTransaction addDailyBonus() {
        if (!canReceiveBonus())
            throw new IllegalStateException("Ya recibiste tu bono diario");
        this.amount = this.amount.add(DAILY_BONUS);
        this.lastBonusDate = LocalDate.now();
        return new WalletTransaction(userId, DAILY_BONUS, TransactionType.BONUS, "Bono diario");
    }

    public WalletTransaction placeBet(Amount betAmount) {
        this.amount = this.amount.subtract(betAmount);
        return new WalletTransaction(userId, betAmount, TransactionType.BET, "Apuesta de partida");
    }

    public WalletTransaction receiveWin(Amount winAmount) {
        this.amount = this.amount.add(winAmount);
        return new WalletTransaction(userId, winAmount, TransactionType.WIN, "Premio de partida");
    }

    public WalletTransaction registerLoss(Amount lossAmount) {
        // Solo registra en historial, no modifica saldo (ya descontado en placeBet)
        return new WalletTransaction(userId, lossAmount, TransactionType.LOSS, "Pérdida de partida");
    }

    public boolean canReceiveBonus() {
        return lastBonusDate == null || lastBonusDate.isBefore(LocalDate.now());
    }

    public boolean hasSufficientFunds(Amount required) {
        return this.amount.isGreaterThanOrEqual(required);
    }

    public LocalDateTime nextBonusAt() {
        if (lastBonusDate == null) return null;
        return lastBonusDate.plusDays(1).atStartOfDay();
    }

    public String getUserId() { return userId; }
    public Amount getAmount() { return amount; }
    public LocalDate getLastBonusDate() { return lastBonusDate; }
    public static Amount getDailyBonus() { return DAILY_BONUS; }
    public static Amount getInitialBalance() { return INITIAL_BALANCE; }
}