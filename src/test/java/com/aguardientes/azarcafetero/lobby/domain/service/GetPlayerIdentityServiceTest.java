package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
import com.aguardientes.azarcafetero.lobby.infrastructure.client.WalletClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPlayerIdentityService — UseCase unit tests")
class GetPlayerIdentityServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private WalletClient walletClient;

    private GetPlayerIdentityService service;

    private static final String USERNAME     = "user@example.com";
    private static final String DISPLAY_NAME = "Juan García";
    private static final String AVATAR_URL   = "https://cdn.example.com/avatar.jpg";
    private static final BigDecimal BALANCE  = new BigDecimal("500.00");
    private static final String JWT_TOKEN    = "fake-jwt-token";

    @BeforeEach
    void setUp() {
        service = new GetPlayerIdentityService(playerRepository, walletClient);
    }

    // ── Existing player ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Returns identity of an existing player using wallet balance")
    void execute_existingPlayer_returnsIdentity() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, DISPLAY_NAME, AVATAR_URL, BALANCE);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));
        when(walletClient.getBalance(JWT_TOKEN)).thenReturn(BALANCE);

        PlayerIdentityDTO result = service.execute(USERNAME, DISPLAY_NAME, AVATAR_URL, JWT_TOKEN);

        assertThat(result.getName()).isEqualTo(DISPLAY_NAME);
        assertThat(result.getAvatar()).isEqualTo(AVATAR_URL);
        assertThat(result.getBalance()).isEqualByComparingTo(BALANCE);
    }

    @Test
    @DisplayName("Does NOT create a new player when one already exists")
    void execute_existingPlayer_doesNotSave() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, DISPLAY_NAME, AVATAR_URL, BALANCE);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));
        when(walletClient.getBalance(JWT_TOKEN)).thenReturn(BALANCE);

        service.execute(USERNAME, DISPLAY_NAME, AVATAR_URL, JWT_TOKEN);

        verify(playerRepository, never()).saveNew(any());
        verify(playerRepository, never()).save(any());
    }

    // ── New player (lazy registration) ───────────────────────────────────────

    @Test
    @DisplayName("Creates and saves a new player on first login")
    void execute_newPlayer_savesAndReturnsIdentity() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(walletClient.getBalance(JWT_TOKEN)).thenReturn(new BigDecimal("500"));

        PlayerIdentityDTO result = service.execute(USERNAME, DISPLAY_NAME, AVATAR_URL, JWT_TOKEN);

        verify(playerRepository, times(1)).saveNew(any(Player.class));
        assertThat(result.getName()).isEqualTo(DISPLAY_NAME);
        assertThat(result.getAvatar()).isEqualTo(AVATAR_URL);
    }

    @Test
    @DisplayName("New player starts with default balance of 500")
    void execute_newPlayer_startsWithDefaultBalance() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(walletClient.getBalance(JWT_TOKEN)).thenReturn(new BigDecimal("500"));

        PlayerIdentityDTO result = service.execute(USERNAME, DISPLAY_NAME, AVATAR_URL, JWT_TOKEN);

        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("500"));
    }

    @Test
    @DisplayName("Uses username as display name when name claim is blank")
    void execute_newPlayer_fallsBackToUsernameWhenNameIsBlank() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(walletClient.getBalance(JWT_TOKEN)).thenReturn(new BigDecimal("500"));

        PlayerIdentityDTO result = service.execute(USERNAME, "  ", AVATAR_URL, JWT_TOKEN);

        assertThat(result.getName()).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("Uses username as display name when name claim is null")
    void execute_newPlayer_fallsBackToUsernameWhenNameIsNull() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(walletClient.getBalance(JWT_TOKEN)).thenReturn(new BigDecimal("500"));

        PlayerIdentityDTO result = service.execute(USERNAME, null, null, JWT_TOKEN);

        assertThat(result.getName()).isEqualTo(USERNAME);
    }

    // ── Wallet fallback ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Falls back to lobby balance when wallet is unavailable")
    void execute_walletUnavailable_fallsBackToLobbyBalance() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, DISPLAY_NAME, AVATAR_URL, BALANCE);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));
        when(walletClient.getBalance(anyString())).thenThrow(new RuntimeException("wallet down"));

        PlayerIdentityDTO result = service.execute(USERNAME, DISPLAY_NAME, AVATAR_URL, JWT_TOKEN);

        assertThat(result.getBalance()).isEqualByComparingTo(BALANCE);
    }

    // ── Race condition handling ───────────────────────────────────────────────

    @Test
    @DisplayName("Recovers from concurrent-creation race condition (DataIntegrityViolationException)")
    void execute_racecondition_fallsBackToFindByUsername() {
        Player raceCreatedPlayer = Player.from(UUID.randomUUID(), USERNAME, DISPLAY_NAME, AVATAR_URL, BALANCE);

        when(playerRepository.findByUsername(USERNAME))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(raceCreatedPlayer));

        doThrow(new DataIntegrityViolationException("duplicate key"))
                .when(playerRepository).saveNew(any());

        when(walletClient.getBalance(JWT_TOKEN)).thenReturn(BALANCE);

        PlayerIdentityDTO result = service.execute(USERNAME, DISPLAY_NAME, AVATAR_URL, JWT_TOKEN);

        assertThat(result.getName()).isEqualTo(DISPLAY_NAME);
        verify(playerRepository, times(2)).findByUsername(USERNAME);
    }

    // ── Performance ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("UseCase responds in under 2 seconds (PBI criterion)")
    void execute_respondsWithinTwoSeconds() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, DISPLAY_NAME, AVATAR_URL, BALANCE);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));
        when(walletClient.getBalance(JWT_TOKEN)).thenReturn(BALANCE);

        long start = System.currentTimeMillis();
        service.execute(USERNAME, DISPLAY_NAME, AVATAR_URL, JWT_TOKEN);
        long elapsed = System.currentTimeMillis() - start;

        assertThat(elapsed).as("UseCase must complete in < 2000 ms").isLessThan(2000L);
    }
}