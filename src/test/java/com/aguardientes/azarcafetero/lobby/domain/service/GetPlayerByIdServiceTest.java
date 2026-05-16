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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPlayerByIdService unit tests")
class GetPlayerByIdServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private GetPlayerByIdService service;

    @BeforeEach
    void setUp() {
        service = new GetPlayerByIdService(playerRepository);
    }

    @Test
    @DisplayName("Should return player when ID exists")
    void execute_playerExists_shouldReturnPlayer() {
        UUID playerId = UUID.randomUUID();
        Player player = Player.from(playerId, "testuser", "Test User", null, BigDecimal.valueOf(500));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        Player result = service.execute(playerId);

        assertThat(result).isEqualTo(player);
    }

    @Test
    @DisplayName("Should throw exception when player ID does not exist")
    void execute_playerNotFound_shouldThrowException() {
        UUID playerId = UUID.randomUUID();
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(playerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player not found");
    }
}
