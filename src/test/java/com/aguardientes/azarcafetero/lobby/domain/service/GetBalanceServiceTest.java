package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Amount;
import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetBalanceService unit tests")
class GetBalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    private GetBalanceService service;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        service = new GetBalanceService(balanceRepository);
    }

    @Test
    @DisplayName("Should return existing balance if present")
    void execute_shouldReturnExistingBalance() {
        Balance balance = new Balance(USER_ID, Amount.of(750), null);
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(balance));

        Balance result = service.execute(USER_ID);

        assertThat(result).isEqualTo(balance);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create and return new balance if not present")
    void execute_shouldCreateAndReturnNewBalance() {
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        Balance result = service.execute(USER_ID);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getAmount()).isEqualTo(Amount.of(500)); // Default initial
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    @DisplayName("Should handle concurrent creation by retrying findByUserId on DataIntegrityViolation")
    void execute_concurrentCreation_shouldRetry() {
        when(balanceRepository.findByUserId(USER_ID))
                .thenReturn(Optional.empty()) // First call in execute
                .thenReturn(Optional.of(new Balance(USER_ID, Amount.of(500), null))); // Second call after exception

        doThrow(new DataIntegrityViolationException("Duplicate key"))
                .when(balanceRepository).save(any(Balance.class));

        Balance result = service.execute(USER_ID);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        verify(balanceRepository, times(2)).findByUserId(USER_ID);
    }
}
