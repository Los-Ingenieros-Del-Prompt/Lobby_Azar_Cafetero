package com.aguardientes.azarcafetero.lobby.infrastructure.web;

import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.CheckZeroBalanceUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerIdentityUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetUpdatedBalanceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final GetPlayerIdentityUseCase getPlayerIdentityUseCase;
    private final GetUpdatedBalanceUseCase getUpdatedBalanceUseCase;
    private final CheckZeroBalanceUseCase checkZeroBalanceUseCase;

    public PlayerController(GetPlayerIdentityUseCase getPlayerIdentityUseCase,
                            GetUpdatedBalanceUseCase getUpdatedBalanceUseCase,
                            CheckZeroBalanceUseCase checkZeroBalanceUseCase) {
        this.getPlayerIdentityUseCase = getPlayerIdentityUseCase;
        this.getUpdatedBalanceUseCase = getUpdatedBalanceUseCase;
        this.checkZeroBalanceUseCase = checkZeroBalanceUseCase;
    }

    @GetMapping("/identity")
    public ResponseEntity<PlayerIdentityDTO> getIdentity(Authentication auth) {
        PlayerIdentityDTO identity = getPlayerIdentityUseCase.execute(auth.getName());
        return ResponseEntity.ok(identity);
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getUpdatedBalance(Authentication auth) {
        BigDecimal balance = getUpdatedBalanceUseCase.execute(auth.getName());
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @GetMapping("/balance/check-zero")
    public ResponseEntity<Map<String, Boolean>> checkZeroBalance(Authentication auth) {
        boolean isZero = checkZeroBalanceUseCase.execute(auth.getName());
        return ResponseEntity.ok(Map.of("zeroBalance", isZero));
    }
}
