package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUpdatedBalanceService — UseCase unit tests")
class GetUpdatedBalanceServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private GetUpdatedBalanceService service;

    private static final String USERNAME = "user@example.com";

    @BeforeEach
    void setUp() {
        service = new GetUpdatedBalanceService(playerRepository);
    }

    // ── Normal balance values ─────────────────────────────────────────────────

    @Test
    @DisplayName("Returns the player's current balance")
    void execute_returnsBalance() {
        BigDecimal expectedBalance = new BigDecimal("1250.75");
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, expectedBalance);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        BigDecimal result = service.execute(USERNAME);

        assertThat(result).isEqualByComparingTo(expectedBalance);
    }

    @Test
    @DisplayName("Returns zero when player balance is exactly 0")
    void execute_returnsZeroBalance() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, BigDecimal.ZERO);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        BigDecimal result = service.execute(USERNAME);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Returns default starting balance of 500 for a new player")
    void execute_returnsDefaultStartingBalance() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, new BigDecimal("500.00"));
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        BigDecimal result = service.execute(USERNAME);

        assertThat(result).isEqualByComparingTo(new BigDecimal("500"));
    }

    @Test
    @DisplayName("Returns updated balance after a game (simulates real-time refresh)")
    void execute_returnsUpdatedBalanceAfterGame() {
        // Simulate player returning from a game with reduced balance
        BigDecimal postGameBalance = new BigDecimal("320.00");
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, postGameBalance);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        BigDecimal result = service.execute(USERNAME);

        assertThat(result).isEqualByComparingTo(postGameBalance);
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
        Player player = Player.from(UUID.randomUUID(), USERNAME, "Player", null, new BigDecimal("500"));
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        long start = System.currentTimeMillis();
        service.execute(USERNAME);
        long elapsed = System.currentTimeMillis() - start;

        assertThat(elapsed).as("UseCase must complete in < 2000 ms").isLessThan(2000L);
    }
}
