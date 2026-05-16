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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaceBetService unit tests")
class PlaceBetServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    private PlaceBetService service;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        service = new PlaceBetService(balanceRepository, transactionRepository);
    }

    @Test
    @DisplayName("Should deduct balance and save transaction when betting with sufficient funds")
    void execute_shouldDeductBalanceAndSaveTransaction() {
        Amount initialAmount = Amount.of(1000);
        Amount betAmount = Amount.of(200);
        Balance balance = new Balance(USER_ID, initialAmount, null);
        
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(balance));

        WalletTransaction result = service.execute(USER_ID, betAmount);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getAmount()).isEqualTo(betAmount);
        assertThat(result.getType()).isEqualTo(TransactionType.BET);
        assertThat(balance.getAmount()).isEqualTo(Amount.of(800));

        verify(balanceRepository).save(balance);
        verify(transactionRepository).save(result);
    }

    @Test
    @DisplayName("Should throw exception when betting with insufficient funds")
    void execute_insufficientFunds_shouldThrowException() {
        Amount initialAmount = Amount.of(100);
        Amount betAmount = Amount.of(200);
        Balance balance = new Balance(USER_ID, initialAmount, null);
        
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> service.execute(USER_ID, betAmount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Saldo insuficiente");

        verify(balanceRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create balance if not exists and then fail if default balance is insufficient")
    void execute_shouldCreateBalanceIfNotExistsAndCheckFunds() {
        // Default initial balance is 500
        Amount betAmount = Amount.of(600);
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(USER_ID, betAmount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Saldo insuficiente");

        // Should have saved the initial balance creation
        verify(balanceRepository).save(any(Balance.class));
    }
}
