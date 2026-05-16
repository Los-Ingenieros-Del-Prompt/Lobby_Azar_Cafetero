package com.aguardientes.azarcafetero.lobby.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain model unit tests")
class DomainModelTest {

    // ── Amount ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Amount — null value throws")
    void amount_nullThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Amount(null));
    }

    @Test
    void amount_negativeThrows() {
        BigDecimal negative = BigDecimal.valueOf(-1);
        assertThrows(IllegalArgumentException.class, () -> new Amount(negative));
    }

    @Test
    @DisplayName("Amount — subtract below zero throws")
    void amount_subtractInsufficientFunds() {
        Amount a = Amount.of(10);
        Amount b = Amount.of(20);
        assertThrows(IllegalStateException.class, () -> a.subtract(b));
    }

    @Test
    @DisplayName("Amount — equals and hashCode are value-based")
    void amount_equalsAndHashCode() {
        Amount a = new Amount(new BigDecimal("100.00"));
        Amount b = new Amount(new BigDecimal("100"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Object());
    }

    @Test
    @DisplayName("Amount — toString returns plain string")
    void amount_toString() {
        assertEquals("100", Amount.of(100).toString());
    }

    // ── WalletTransaction ─────────────────────────────────────────────────────

    @Test
    void walletTransaction_blankUserIdThrows() {
        Amount amount = Amount.of(100);
        assertThrows(IllegalArgumentException.class,
                () -> new WalletTransaction("  ", amount, TransactionType.BET, "desc"));
    }

    @Test
    @DisplayName("WalletTransaction — null amount throws")
    void walletTransaction_nullAmountThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new WalletTransaction("user", null, TransactionType.BET, "desc"));
    }

    @Test
    void walletTransaction_nullTypeThrows() {
        Amount amount = Amount.of(100);
        assertThrows(IllegalArgumentException.class,
                () -> new WalletTransaction("user", amount, null, "desc"));
    }

    @Test
    @DisplayName("WalletTransaction — full-arg constructor for persistence")
    void walletTransaction_persistenceConstructor() {
        String txId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        WalletTransaction tx = new WalletTransaction(txId, "user", Amount.of(50), TransactionType.WIN, now, "win");
        assertEquals(txId, tx.getTransactionId());
        assertEquals("user", tx.getUserId());
        assertEquals(TransactionType.WIN, tx.getType());
        assertEquals(now, tx.getCreatedAt());
        assertEquals("win", tx.getDescription());
    }

    // ── Player ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Player — goOnline transitions status")
    void player_goOnline() {
        Player p = Player.create(UUID.randomUUID(), "user1");
        p.goOnline();
        assertEquals(PlayerStatus.ONLINE, p.getStatus());
    }

    @Test
    @DisplayName("Player — goOnline twice throws")
    void player_goOnlineTwiceThrows() {
        Player p = Player.create(UUID.randomUUID(), "user1");
        p.goOnline();
        assertThrows(IllegalStateException.class, p::goOnline);
    }

    @Test
    @DisplayName("Player — enterRoom transitions status")
    void player_enterRoom() {
        Player p = Player.create(UUID.randomUUID(), "user1");
        p.goOnline();
        p.enterRoom();
        assertEquals(PlayerStatus.IN_ROOM, p.getStatus());
    }

    @Test
    @DisplayName("Player — enterRoom without goOnline throws")
    void player_enterRoomWithoutOnlineThrows() {
        Player p = Player.create(UUID.randomUUID(), "user1");
        assertThrows(IllegalStateException.class, p::enterRoom);
    }

    @Test
    @DisplayName("Player — getDisplayName falls back to username when null")
    void player_displayNameFallback() {
        Player p = Player.from(UUID.randomUUID(), "user1", null, null, BigDecimal.TEN);
        assertEquals("user1", p.getDisplayName());
    }

    // ── PlayerDTO ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PlayerDTO — setters and toString")
    void playerDTO_settersAndToString() {
        PlayerDTO dto = new PlayerDTO();
        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setUsername("u");
        dto.setDisplayName("User");
        dto.setAvatar("http://a");
        dto.setBalance(BigDecimal.valueOf(100));

        assertEquals(id, dto.getId());
        assertEquals("u", dto.getUsername());
        assertEquals("User", dto.getDisplayName());
        assertEquals("http://a", dto.getAvatar());
        assertEquals(BigDecimal.valueOf(100), dto.getBalance());
        assertTrue(dto.toString().contains("u"));
    }
}
