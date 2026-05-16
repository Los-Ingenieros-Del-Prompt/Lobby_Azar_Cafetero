package com.aguardientes.azarcafetero.lobby.infrastructure.web;

import com.aguardientes.azarcafetero.lobby.domain.model.Amount;
import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerByIdUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerIdentityUseCase;
import com.aguardientes.azarcafetero.lobby.domain.service.*;
import com.aguardientes.azarcafetero.lobby.infrastructure.sse.BalanceSseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;
import org.springframework.security.core.Authentication;

import com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction;
import com.aguardientes.azarcafetero.lobby.domain.model.TransactionType;
import com.aguardientes.azarcafetero.lobby.infrastructure.security.JwtAuthDetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerController unit tests")
class PlayerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GetPlayerIdentityUseCase getPlayerIdentityUseCase;
    @Mock
    private GetPlayerByIdUseCase getPlayerByIdUseCase;
    @Mock
    private GetBalanceService getBalanceService;
    @Mock
    private AddDailyBonusService addDailyBonusService;
    @Mock
    private PlaceBetService placeBetService;
    @Mock
    private ReceiveWinService receiveWinService;
    @Mock
    private RegisterLossService registerLossService;
    @Mock
    private GetTransactionHistoryService getTransactionHistoryService;
    @Mock
    private BalanceSseService balanceSseService;

    @InjectMocks
    private PlayerController playerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(playerController).build();
        ReflectionTestUtils.setField(playerController, "internalApiKey", "test-api-key");
    }

    @Test
    @DisplayName("POST /api/player/bet — Should place bet")
    void placeBet_shouldReturnSuccess() throws Exception {
        com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction tx = 
            new com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction("testuser", Amount.of(100), 
            com.aguardientes.azarcafetero.lobby.domain.model.TransactionType.BET, "Apuesta");
        Balance balance = new Balance("testuser", Amount.of(400), null);
        
        when(placeBetService.execute(anyString(), any())).thenReturn(tx);
        when(getBalanceService.execute(anyString())).thenReturn(balance);

        String body = "{\"userId\":\"testuser\", \"amount\":100}";
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/player/bet")
                .header("X-Internal-Key", "test-api-key")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/player/balance — Should return current balance")
    void getBalance_shouldReturnBalance() throws Exception {
        Balance balance = new Balance("testuser", Amount.of(500), null);
        when(getBalanceService.execute(anyString())).thenReturn(balance);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        mockMvc.perform(get("/api/player/balance").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("testuser"))
                .andExpect(jsonPath("$.amount").value(500));
    }

    @Test
    @DisplayName("GET /api/player/{id}/internal — Should return player details")
    void getPlayerById_shouldReturnPlayer() throws Exception {
        UUID playerId = UUID.randomUUID();
        Player player = Player.from(playerId, "user", "Name", null, BigDecimal.valueOf(500));
        when(getPlayerByIdUseCase.execute(playerId)).thenReturn(player);

        mockMvc.perform(get("/api/player/" + playerId + "/internal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @DisplayName("GET /api/player/history — Should return transaction history")
    void getHistory_shouldReturnHistory() throws Exception {
        when(getTransactionHistoryService.execute(anyString())).thenReturn(java.util.List.of());

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        mockMvc.perform(get("/api/player/history").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/player/bonus — Should claim daily bonus")
    void claimBonus_shouldReturnSuccess() throws Exception {
        Balance balance = new Balance("testuser", Amount.of(600), java.time.LocalDate.now());
        WalletTransaction tx = new WalletTransaction("testuser", Amount.of(100), TransactionType.BONUS, "Bono");

        when(addDailyBonusService.execute(anyString())).thenReturn(tx);
        when(getBalanceService.execute(anyString())).thenReturn(balance);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        mockMvc.perform(post("/api/player/bonus").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Bono diario reclamado"));
    }

    @Test
    @DisplayName("GET /api/player/identity — Should return player identity")
    void getIdentity_shouldReturnIdentity() throws Exception {
        PlayerIdentityDTO identity = new PlayerIdentityDTO("TestName", "http://avatar.url", java.math.BigDecimal.valueOf(500));
        Balance balance = new Balance("testuser", Amount.of(500), null);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(auth.getDetails()).thenReturn(new JwtAuthDetails("TestName", "http://avatar.url"));
        when(getBalanceService.execute("testuser")).thenReturn(balance);
        when(getPlayerIdentityUseCase.execute(anyString(), anyString(), anyString(), any())).thenReturn(identity);

        mockMvc.perform(get("/api/player/identity")
                .header("Authorization", "Bearer test-token")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestName"));
    }

    @Test
    @DisplayName("GET /api/player/balance/check-zero — Should return false when balance is positive")
    void checkZeroBalance_notZero() throws Exception {
        Balance balance = new Balance("testuser", Amount.of(100), null);
        when(getBalanceService.execute(anyString())).thenReturn(balance);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        mockMvc.perform(get("/api/player/balance/check-zero").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zeroBalance").value(false));
    }

    @Test
    @DisplayName("GET /api/player/balance/check-zero — Should return true when balance is zero")
    void checkZeroBalance_isZero() throws Exception {
        Balance balance = new Balance("testuser", Amount.of(0), null);
        when(getBalanceService.execute(anyString())).thenReturn(balance);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        mockMvc.perform(get("/api/player/balance/check-zero").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zeroBalance").value(true));
    }

    @Test
    @DisplayName("POST /api/player/win — Should credit win amount")
    void receiveWin_shouldReturnSuccess() throws Exception {
        WalletTransaction tx = new WalletTransaction("testuser", Amount.of(200), TransactionType.WIN, "Premio");
        Balance balance = new Balance("testuser", Amount.of(700), null);

        when(receiveWinService.execute(anyString(), any())).thenReturn(tx);
        when(getBalanceService.execute(anyString())).thenReturn(balance);

        String body = "{\"userId\":\"testuser\", \"amount\":200}";
        mockMvc.perform(post("/api/player/win")
                .header("X-Internal-Key", "test-api-key")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Premio acreditado"));
    }

    @Test
    @DisplayName("POST /api/player/win — Should return 401 when key is invalid")
    void receiveWin_unauthorized() throws Exception {
        mockMvc.perform(post("/api/player/win")
                .header("X-Internal-Key", "wrong-key")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"testuser\", \"amount\":200}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/player/loss — Should register loss")
    void registerLoss_shouldReturnSuccess() throws Exception {
        WalletTransaction tx = new WalletTransaction("testuser", Amount.of(50), TransactionType.LOSS, "Pérdida");

        when(registerLossService.execute(anyString(), any())).thenReturn(tx);

        String body = "{\"userId\":\"testuser\", \"amount\":50}";
        mockMvc.perform(post("/api/player/loss")
                .header("X-Internal-Key", "test-api-key")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pérdida registrada"));
    }

    @Test
    @DisplayName("POST /api/player/loss — Should return 401 when key is invalid")
    void registerLoss_unauthorized() throws Exception {
        mockMvc.perform(post("/api/player/loss")
                .header("X-Internal-Key", "wrong-key")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"testuser\", \"amount\":50}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/player/bet — Should return 401 when key is invalid")
    void placeBet_unauthorized() throws Exception {
        mockMvc.perform(post("/api/player/bet")
                .header("X-Internal-Key", "wrong-key")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"testuser\", \"amount\":100}"))
                .andExpect(status().isUnauthorized());
    }
}
