package com.aguardientes.azarcafetero.lobby.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Balance domain tests")
class BalanceTest {

    @Test
    @DisplayName("Constructor — null userId throws")
    void constructor_nullUserIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Balance(null));
    }

    @Test
    @DisplayName("Constructor — blank userId throws")
    void constructor_blankUserIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Balance("  "));
    }

    @Test
    @DisplayName("Constructor — default starting balance is 500")
    void constructor_defaultBalance() {
        Balance b = new Balance("user1");
        assertEquals(Amount.of(500), b.getAmount());
        assertNull(b.getLastBonusDate());
        assertEquals("user1", b.getUserId());
    }

    @Test
    @DisplayName("Persistence constructor — preserves passed values")
    void persistenceConstructor() {
        Amount amount = Amount.of(1234);
        LocalDate today = LocalDate.now();
        Balance b = new Balance("user1", amount, today);
        assertEquals(amount, b.getAmount());
        assertEquals(today, b.getLastBonusDate());
    }

    @Test
    @DisplayName("canReceiveBonus — true when lastBonusDate is null")
    void canReceiveBonus_nullDate() {
        Balance b = new Balance("user1");
        assertTrue(b.canReceiveBonus());
    }

    @Test
    @DisplayName("canReceiveBonus — false when bonus was today")
    void canReceiveBonus_today() {
        Balance b = new Balance("user1", Amount.of(500), LocalDate.now());
        assertFalse(b.canReceiveBonus());
    }

    @Test
    @DisplayName("canReceiveBonus — true when bonus was yesterday")
    void canReceiveBonus_yesterday() {
        Balance b = new Balance("user1", Amount.of(500), LocalDate.now().minusDays(1));
        assertTrue(b.canReceiveBonus());
    }

    @Test
    @DisplayName("hasSufficientFunds — returns true when balance is enough")
    void hasSufficientFunds_true() {
        Balance b = new Balance("user1");
        assertTrue(b.hasSufficientFunds(Amount.of(500)));
        assertTrue(b.hasSufficientFunds(Amount.of(100)));
    }

    @Test
    @DisplayName("hasSufficientFunds — returns false when balance is insufficient")
    void hasSufficientFunds_false() {
        Balance b = new Balance("user1");
        assertFalse(b.hasSufficientFunds(Amount.of(501)));
    }

    @Test
    @DisplayName("nextBonusAt — returns null when no bonus was claimed")
    void nextBonusAt_nullWhenNoClaim() {
        Balance b = new Balance("user1");
        assertNull(b.nextBonusAt());
    }

    @Test
    @DisplayName("nextBonusAt — returns next-day midnight when bonus was claimed")
    void nextBonusAt_returnsNextDay() {
        LocalDate today = LocalDate.now();
        Balance b = new Balance("user1", Amount.of(500), today);
        LocalDateTime expected = today.plusDays(1).atStartOfDay();
        assertEquals(expected, b.nextBonusAt());
    }

    @Test
    @DisplayName("addDailyBonus — throws when bonus was already claimed")
    void addDailyBonus_alreadyClaimedThrows() {
        Balance b = new Balance("user1", Amount.of(500), LocalDate.now());
        assertThrows(IllegalStateException.class, b::addDailyBonus);
    }

    @Test
    @DisplayName("addDailyBonus — adds bonus and updates lastBonusDate")
    void addDailyBonus_success() {
        Balance b = new Balance("user1");
        WalletTransaction tx = b.addDailyBonus();
        assertEquals(Amount.of(600), b.getAmount());
        assertEquals(LocalDate.now(), b.getLastBonusDate());
        assertEquals(TransactionType.BONUS, tx.getType());
    }

    @Test
    @DisplayName("placeBet — deducts amount and returns BET transaction")
    void placeBet_success() {
        Balance b = new Balance("user1");
        WalletTransaction tx = b.placeBet(Amount.of(100));
        assertEquals(Amount.of(400), b.getAmount());
        assertEquals(TransactionType.BET, tx.getType());
        assertEquals(Amount.of(100), tx.getAmount());
    }

    @Test
    @DisplayName("receiveWin — adds amount and returns WIN transaction")
    void receiveWin_success() {
        Balance b = new Balance("user1");
        WalletTransaction tx = b.receiveWin(Amount.of(200));
        assertEquals(Amount.of(700), b.getAmount());
        assertEquals(TransactionType.WIN, tx.getType());
    }

    @Test
    @DisplayName("registerLoss — does NOT modify balance but returns LOSS transaction")
    void registerLoss_doesNotModifyBalance() {
        Balance b = new Balance("user1");
        WalletTransaction tx = b.registerLoss(Amount.of(100));
        assertEquals(Amount.of(500), b.getAmount());
        assertEquals(TransactionType.LOSS, tx.getType());
        assertEquals(Amount.of(100), tx.getAmount());
    }

    @Test
    @DisplayName("Static getters expose constants")
    void staticGetters() {
        assertEquals(Amount.of(100), Balance.getDailyBonus());
        assertEquals(Amount.of(500), Balance.getInitialBalance());
    }
}
