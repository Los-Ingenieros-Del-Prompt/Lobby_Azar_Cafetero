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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterLossService unit tests")
class RegisterLossServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    private RegisterLossService service;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        service = new RegisterLossService(balanceRepository, transactionRepository);
    }

    @Test
    @DisplayName("Should register loss transaction (balance remains same as it was deducted at bet)")
    void execute_shouldRegisterLossTransaction() {
        Amount initialAmount = Amount.of(500);
        Amount lossAmount = Amount.of(200);
        Balance balance = new Balance(USER_ID, initialAmount, null);
        
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(balance));

        WalletTransaction result = service.execute(USER_ID, lossAmount);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getAmount()).isEqualTo(lossAmount);
        assertThat(result.getType()).isEqualTo(TransactionType.LOSS);
        assertThat(balance.getAmount()).isEqualTo(initialAmount); // Balance doesn't change on loss (it changed on bet)

        verify(transactionRepository).save(result);
        // Note: RegisterLoss doesn't save balance because it doesn't change it
    }

    @Test
    @DisplayName("Should create balance if not exists and then register loss")
    void execute_shouldCreateBalanceIfNotExists() {
        Amount lossAmount = Amount.of(100);
        when(balanceRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        service.execute(USER_ID, lossAmount);

        verify(balanceRepository).save(any(Balance.class));
        verify(transactionRepository).save(any(WalletTransaction.class));
    }
}
