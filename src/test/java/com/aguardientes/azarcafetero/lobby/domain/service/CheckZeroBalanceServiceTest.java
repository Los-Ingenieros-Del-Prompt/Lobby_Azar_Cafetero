package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckZeroBalanceService — UseCase unit tests")
class CheckZeroBalanceServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private CheckZeroBalanceService service;

    private static final String USERNAME = "user@example.com";

    @BeforeEach
    void setUp() {
        service = new CheckZeroBalanceService(playerRepository);
    }

    // ── Zero balance (CTA should appear) ─────────────────────────────────────

    @Test
    @DisplayName("Returns true when player balance is exactly 0")
    void execute_balanceIsZero_returnsTrue() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, BigDecimal.ZERO);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        assertThat(service.execute(USERNAME)).isTrue();
    }

    @Test
    @DisplayName("Returns true when balance is 0.00 (BigDecimal scale variant)")
    void execute_balanceIsZeroWithScale_returnsTrue() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, new BigDecimal("0.00"));
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        assertThat(service.execute(USERNAME)).isTrue();
    }

    // ── Non-zero balance (CTA should NOT appear) ──────────────────────────────

    @Test
    @DisplayName("Returns false when player has a positive balance")
    void execute_balanceIsPositive_returnsFalse() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, new BigDecimal("500"));
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        assertThat(service.execute(USERNAME)).isFalse();
    }

    @ParameterizedTest(name = "balance = {0} → false")
    @ValueSource(strings = {"0.01", "1", "500", "9999.99"})
    @DisplayName("Returns false for any positive balance")
    void execute_variousPositiveBalances_returnsFalse(String balanceStr) {
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, new BigDecimal(balanceStr));
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        assertThat(service.execute(USERNAME)).isFalse();
    }

    // ── Player not found ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Throws IllegalArgumentException when player is not found")
    void execute_playerNotFound_throwsException() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(USERNAME))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(USERNAME);
    }

    // ── Performance ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("UseCase responds in under 2 seconds (PBI criterion)")
    void execute_respondsWithinTwoSeconds() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, BigDecimal.ZERO);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        long start = System.currentTimeMillis();
        service.execute(USERNAME);
        long elapsed = System.currentTimeMillis() - start;

        assertThat(elapsed).as("UseCase must complete in < 2000 ms").isLessThan(2000L);
    }
}
