package com.aguardientes.azarcafetero.lobby.infrastructure.client;

import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.util.Map;

public class WalletClient {

    private final RestClient restClient;

    public WalletClient(String walletServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(walletServiceUrl)
                .build();
    }

    public BigDecimal getBalance(String jwtToken) {
        var response = restClient.get()
                .uri("/player/balance")
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(Map.class);
        Object amount = response != null ? response.get("amount") : null;
        if (amount instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }
}