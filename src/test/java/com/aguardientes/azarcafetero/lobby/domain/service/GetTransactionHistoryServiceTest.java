package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Amount;
import com.aguardientes.azarcafetero.lobby.domain.model.TransactionType;
import com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction;
import com.aguardientes.azarcafetero.lobby.domain.port.out.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTransactionHistoryService unit tests")
class GetTransactionHistoryServiceTest {

    @Mock
    private WalletTransactionRepository transactionRepository;

    private GetTransactionHistoryService service;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        service = new GetTransactionHistoryService(transactionRepository);
    }

    @Test
    @DisplayName("Should return transaction list for user")
    void execute_shouldReturnTransactionHistory() {
        WalletTransaction tx1 = new WalletTransaction(USER_ID, Amount.of(100), TransactionType.BONUS, "Bono");
        WalletTransaction tx2 = new WalletTransaction(USER_ID, Amount.of(50), TransactionType.BET, "Apuesta");
        
        when(transactionRepository.findByUserId(USER_ID)).thenReturn(List.of(tx1, tx2));

        List<WalletTransaction> result = service.execute(USER_ID);

        assertThat(result)
            .hasSize(2)
            .containsExactly(tx1, tx2);
    }

    @Test
    @DisplayName("Should throw exception when userId is blank")
    void execute_blankUserId_shouldThrowException() {
        assertThatThrownBy(() -> service.execute(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El userId no puede estar vacío");
    }
}
