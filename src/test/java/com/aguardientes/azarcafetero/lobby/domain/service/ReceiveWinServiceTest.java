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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReceiveWinService unit tests")
class ReceiveWinServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    private ReceiveWinService service;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        service = new ReceiveWinService(balanceRepository, transactionRepository);
    }

    @Test
    @DisplayName("Should update balance and save transaction when winning")
    void execute_shouldUpdateBalanceAndSaveTransaction() {
        Amount initialAmount = Amount.of(500);
        Amount winAmount = Amount.of(200);
        Balance balance = new Balance(USER_ID, initialAmount, null);
        
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(balance));

        WalletTransaction result = service.execute(USER_ID, winAmount);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getAmount()).isEqualTo(winAmount);
        assertThat(result.getType()).isEqualTo(TransactionType.WIN);
        assertThat(balance.getAmount()).isEqualTo(Amount.of(700));

        verify(balanceRepository).save(balance);
        verify(transactionRepository).save(result);
    }

    @Test
    @DisplayName("Should create new balance if not exists and then update")
    void execute_shouldCreateBalanceIfNotExists() {
        Amount winAmount = Amount.of(100);
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        service.execute(USER_ID, winAmount);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository, times(2)).save(balanceCaptor.capture());
        
        Balance savedBalance = balanceCaptor.getValue();
        assertThat(savedBalance.getUserId()).isEqualTo(USER_ID);
        // Initial balance 500 + win 100 = 600
        assertThat(savedBalance.getAmount()).isEqualTo(Amount.of(600));
        
        verify(transactionRepository).save(any(WalletTransaction.class));
    }
}
