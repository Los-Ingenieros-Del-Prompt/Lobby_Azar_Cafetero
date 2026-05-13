
package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "balances")
public class BalanceJpaEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "last_bonus_date")
    private LocalDate lastBonusDate;

    public BalanceJpaEntity() {}

    public BalanceJpaEntity(String userId, BigDecimal amount, LocalDate lastBonusDate) {
        this.userId = userId;
        this.amount = amount;
        this.lastBonusDate = lastBonusDate;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getLastBonusDate() { return lastBonusDate; }
    public void setLastBonusDate(LocalDate lastBonusDate) { this.lastBonusDate = lastBonusDate; }
}


