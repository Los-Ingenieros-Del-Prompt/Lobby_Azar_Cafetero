package com.aguardientes.azarcafetero.lobby.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class TableDTO {

    private final UUID id;
    private final String name;
    private final BigDecimal minBet;
    private final TableState state;
    private final Integer playerCount;

    public TableDTO(UUID id, String name, BigDecimal minBet, TableState state, Integer playerCount) {
        this.id = id;
        this.name = name;
        this.minBet = minBet;
        this.state = state;
        this.playerCount = playerCount;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMinBet() {
        return minBet;
    }

    public TableState getState() {
        return state;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }
}
