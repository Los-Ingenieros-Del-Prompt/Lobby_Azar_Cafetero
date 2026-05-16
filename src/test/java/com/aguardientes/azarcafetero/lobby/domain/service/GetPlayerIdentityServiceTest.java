package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPlayerIdentityService unit tests")
class GetPlayerIdentityServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private BalanceRepository balanceRepository;

    private GetPlayerIdentityService service;

    private static final String USERNAME = "user@example.com";
    private static final String NAME = "Test User";
    private static final String AVATAR = "http://avatar.url";

    @BeforeEach
    void setUp() {
        service = new GetPlayerIdentityService(playerRepository, balanceRepository);
    }

    @Test
    @DisplayName("Should return identity and not create if player exists")
    void execute_playerExists_shouldReturnIdentity() {
        Player player = Player.from(UUID.randomUUID(), USERNAME, NAME, AVATAR, BigDecimal.ZERO);
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(player));

        PlayerIdentityDTO result = service.execute(USERNAME, NAME, AVATAR, "token");

        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getAvatar()).isEqualTo(AVATAR);
        verify(playerRepository, never()).saveNew(any());
        verify(balanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create player and balance if player does not exist")
    void execute_playerDoesNotExist_shouldCreatePlayerAndBalance() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(balanceRepository.findByUserId(USERNAME)).thenReturn(Optional.empty());

        PlayerIdentityDTO result = service.execute(USERNAME, NAME, AVATAR, "token");

        assertThat(result.getName()).isEqualTo(NAME);
        verify(playerRepository).saveNew(any(Player.class));
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    @DisplayName("Should handle concurrent creation using findOrCreate logic")
    void execute_concurrentCreation_shouldHandleException() {
        when(playerRepository.findByUsername(USERNAME))
                .thenReturn(Optional.empty()) // First call in execute
                .thenReturn(Optional.of(Player.from(UUID.randomUUID(), USERNAME, NAME, AVATAR, BigDecimal.ZERO))); // Second call after exception

        doThrow(new DataIntegrityViolationException("Duplicate key"))
                .when(playerRepository).saveNew(any(Player.class));

        PlayerIdentityDTO result = service.execute(USERNAME, NAME, AVATAR, "token");

        assertThat(result.getName()).isEqualTo(NAME);
        verify(playerRepository, times(2)).findByUsername(USERNAME);
    }
}
