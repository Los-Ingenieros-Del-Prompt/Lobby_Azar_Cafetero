package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Amount;
import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import com.aguardientes.azarcafetero.lobby.domain.model.TransactionType;
import com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository;
import com.aguardientes.azarcafetero.lobby.domain.port.out.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddDailyBonusService unit tests")
class AddDailyBonusServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    private AddDailyBonusService service;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        service = new AddDailyBonusService(balanceRepository, transactionRepository);
    }

    @Test
    @DisplayName("Should add bonus and save transaction when bonus is available")
    void execute_shouldAddBonusAndSaveTransaction() {
        Amount initialAmount = Amount.of(500);
        Balance balance = new Balance(USER_ID, initialAmount, null); // lastBonusDate is null
        
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(balance));

        WalletTransaction result = service.execute(USER_ID);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getType()).isEqualTo(TransactionType.BONUS);
        assertThat(balance.getAmount()).isEqualTo(Amount.of(600)); // 500 + 100
        assertThat(balance.getLastBonusDate()).isEqualTo(LocalDate.now());

        verify(balanceRepository).save(balance);
        verify(transactionRepository).save(result);
    }

    @Test
    @DisplayName("Should throw exception when bonus was already claimed today")
    void execute_bonusAlreadyClaimed_shouldThrowException() {
        Amount initialAmount = Amount.of(500);
        Balance balance = new Balance(USER_ID, initialAmount, LocalDate.now());
        
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> service.execute(USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ya recibiste tu bono diario");

        verify(balanceRepository, never()).save(balance);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create balance if not exists and then add bonus")
    void execute_shouldCreateBalanceIfNotExists() {
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        service.execute(USER_ID);

        // Should save once for creation and once for update
        verify(balanceRepository, times(2)).save(any(Balance.class));
        verify(transactionRepository).save(any(WalletTransaction.class));
    }
}
