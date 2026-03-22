package com.aguardientes.azarcafetero.lobby.infrastructure.web;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerDTO;
import com.aguardientes.azarcafetero.lobby.domain.model.PlayerIdentityDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.CheckZeroBalanceUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerByIdUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerIdentityUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetUpdatedBalanceUseCase;
import com.aguardientes.azarcafetero.lobby.infrastructure.security.JwtAuthDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final GetPlayerIdentityUseCase getPlayerIdentityUseCase;
    private final GetUpdatedBalanceUseCase getUpdatedBalanceUseCase;
    private final CheckZeroBalanceUseCase checkZeroBalanceUseCase;
    private final GetPlayerByIdUseCase getPlayerByIdUseCase;

    public PlayerController(GetPlayerIdentityUseCase getPlayerIdentityUseCase,
                            GetUpdatedBalanceUseCase getUpdatedBalanceUseCase,
                            CheckZeroBalanceUseCase checkZeroBalanceUseCase,
                            GetPlayerByIdUseCase getPlayerByIdUseCase) {
        this.getPlayerIdentityUseCase = getPlayerIdentityUseCase;
        this.getUpdatedBalanceUseCase = getUpdatedBalanceUseCase;
        this.checkZeroBalanceUseCase = checkZeroBalanceUseCase;
        this.getPlayerByIdUseCase = getPlayerByIdUseCase;
    }

    @GetMapping("/identity")
    public ResponseEntity<PlayerIdentityDTO> getIdentity(Authentication auth) {
        JwtAuthDetails details = auth.getDetails() instanceof JwtAuthDetails d ? d : new JwtAuthDetails(null, null);
        PlayerIdentityDTO identity = getPlayerIdentityUseCase.execute(auth.getName(), details.getName(), details.getAvatarUrl());
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

    @GetMapping("/{playerId}/internal")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable UUID playerId) {
        Player player = getPlayerByIdUseCase.execute(playerId);
        return ResponseEntity.ok(PlayerDTO.from(player));
    }
}
