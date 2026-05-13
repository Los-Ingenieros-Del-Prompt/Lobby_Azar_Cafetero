package com.aguardientes.azarcafetero.lobby.infrastructure.web;

import com.aguardientes.azarcafetero.lobby.domain.model.*;
import com.aguardientes.azarcafetero.lobby.domain.port.in.*;
import com.aguardientes.azarcafetero.lobby.domain.service.*;
import com.aguardientes.azarcafetero.lobby.infrastructure.security.JwtAuthDetails;
import com.aguardientes.azarcafetero.lobby.infrastructure.sse.BalanceSseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    // — use cases de identidad (ya existían) —
    private final GetPlayerIdentityUseCase getPlayerIdentityUseCase;
    private final GetPlayerByIdUseCase getPlayerByIdUseCase;

    // — servicios de wallet (nuevos) —
    private final GetBalanceService getBalanceService;
    private final AddDailyBonusService addDailyBonusService;
    private final PlaceBetService placeBetService;
    private final ReceiveWinService receiveWinService;
    private final RegisterLossService registerLossService;
    private final GetTransactionHistoryService getTransactionHistoryService;
    private final BalanceSseService balanceSseService;

    public PlayerController(
            GetPlayerIdentityUseCase getPlayerIdentityUseCase,
            GetPlayerByIdUseCase getPlayerByIdUseCase,
            GetBalanceService getBalanceService,
            AddDailyBonusService addDailyBonusService,
            PlaceBetService placeBetService,
            ReceiveWinService receiveWinService,
            RegisterLossService registerLossService,
            GetTransactionHistoryService getTransactionHistoryService,
            BalanceSseService balanceSseService) {
        this.getPlayerIdentityUseCase = getPlayerIdentityUseCase;
        this.getPlayerByIdUseCase = getPlayerByIdUseCase;
        this.getBalanceService = getBalanceService;
        this.addDailyBonusService = addDailyBonusService;
        this.placeBetService = placeBetService;
        this.receiveWinService = receiveWinService;
        this.registerLossService = registerLossService;
        this.getTransactionHistoryService = getTransactionHistoryService;
        this.balanceSseService = balanceSseService;
    }

    // ──────────────────────────────────────────────────────────
    // IDENTIDAD (existentes)
    // ──────────────────────────────────────────────────────────

    @GetMapping("/identity")
    public ResponseEntity<PlayerIdentityDTO> getIdentity(
            Authentication auth,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        JwtAuthDetails details = auth.getDetails() instanceof JwtAuthDetails d
                ? d : new JwtAuthDetails(null, null);

        String jwtToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }

        // Obtener identidad + balance real desde tabla balances
        String userId = auth.getName();
        Balance balance = getBalanceService.execute(userId);

        PlayerIdentityDTO identity = getPlayerIdentityUseCase.execute(
                userId, details.getName(), details.getAvatarUrl(), jwtToken
        );

        // Override balance con el valor real de la tabla balances
        return ResponseEntity.ok(new PlayerIdentityDTO(
                identity.getName(),
                identity.getAvatar(),
                balance.getAmount().getValue()
        ));
    }

    @GetMapping("/{playerId}/internal")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable UUID playerId) {
        Player player = getPlayerByIdUseCase.execute(playerId);
        return ResponseEntity.ok(PlayerDTO.from(player));
    }

    // ──────────────────────────────────────────────────────────
    // BALANCE (absorbidos de wallet)
    // ──────────────────────────────────────────────────────────

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication auth) {
        String userId = auth.getName();
        Balance balance = getBalanceService.execute(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "amount", balance.getAmount().getValue(),
                "canReceiveBonus", balance.canReceiveBonus(),
                "nextBonusAt", balance.nextBonusAt() != null ? balance.nextBonusAt().toString() : null
        ));
    }

    @GetMapping(value = "/balance/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter liveBalance(Authentication auth) {
        return balanceSseService.subscribe(auth.getName());
    }

    @GetMapping("/balance/check-zero")
    public ResponseEntity<Map<String, Boolean>> checkZeroBalance(Authentication auth) {
        Balance balance = getBalanceService.execute(auth.getName());
        boolean isZero = balance.getAmount().getValue().compareTo(BigDecimal.ZERO) == 0;
        return ResponseEntity.ok(Map.of("zeroBalance", isZero));
    }

    @PostMapping("/bonus")
    public ResponseEntity<?> claimDailyBonus(Authentication auth) {
        String userId = auth.getName();
        WalletTransaction tx = addDailyBonusService.execute(userId);
        Balance updated = getBalanceService.execute(userId);
        balanceSseService.notifyBalanceUpdate(userId, updated.getAmount().getValue(),
                tx.getAmount().getValue());
        return ResponseEntity.ok(Map.of(
                "message", "Bono diario reclamado",
                "amount", tx.getAmount().getValue(),
                "transactionId", tx.getTransactionId()
        ));
    }

    // ──────────────────────────────────────────────────────────
    // TRANSACCIONES DE JUEGO (protegidas por X-Internal-Key)
    // Llamadas únicamente por game-ws, brisca, parques
    // ──────────────────────────────────────────────────────────

    @PostMapping("/bet")
    public ResponseEntity<?> placeBet(@RequestBody GameTransactionRequest request) {
        Amount betAmount = new Amount(request.amount());
        WalletTransaction tx = placeBetService.execute(request.userId(), betAmount);
        Balance updated = getBalanceService.execute(request.userId());
        balanceSseService.notifyBalanceUpdate(request.userId(),
                updated.getAmount().getValue(),
                tx.getAmount().getValue().negate());
        return ResponseEntity.ok(Map.of(
                "message", "Apuesta registrada",
                "amount", tx.getAmount().getValue(),
                "transactionId", tx.getTransactionId()
        ));
    }

    @PostMapping("/win")
    public ResponseEntity<?> receiveWin(@RequestBody GameTransactionRequest request) {
        Amount winAmount = new Amount(request.amount());
        WalletTransaction tx = receiveWinService.execute(request.userId(), winAmount);
        Balance updated = getBalanceService.execute(request.userId());
        balanceSseService.notifyBalanceUpdate(request.userId(),
                updated.getAmount().getValue(),
                tx.getAmount().getValue());
        return ResponseEntity.ok(Map.of(
                "message", "Premio acreditado",
                "amount", tx.getAmount().getValue(),
                "transactionId", tx.getTransactionId()
        ));
    }

    @PostMapping("/loss")
    public ResponseEntity<?> registerLoss(@RequestBody GameTransactionRequest request) {
        Amount lossAmount = new Amount(request.amount());
        WalletTransaction tx = registerLossService.execute(request.userId(), lossAmount);
        return ResponseEntity.ok(Map.of(
                "message", "Pérdida registrada",
                "amount", tx.getAmount().getValue(),
                "transactionId", tx.getTransactionId()
        ));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(Authentication auth) {
        List<Map<String, Object>> history = getTransactionHistoryService.execute(auth.getName())
                .stream()
                .map(t -> Map.<String, Object>of(
                        "transactionId", t.getTransactionId(),
                        "amount", t.getAmount().getValue(),
                        "type", t.getType(),
                        "description", t.getDescription(),
                        "createdAt", t.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }
}