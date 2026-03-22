package com.aguardientes.azarcafetero.lobby.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Table {

    private final UUID tableId;
    private final UUID floorId;
    private final String name;
    private final BigDecimal minBet;
    private final TableState state;
    private final Integer playerCount;

    public Table(UUID tableId, UUID floorId, String name, BigDecimal minBet, TableState state, Integer playerCount) {
        this.tableId = tableId;
        this.floorId = floorId;
        this.name = name;
        this.minBet = minBet;
        this.state = state;
        this.playerCount = playerCount;
    }

    public static Table create(UUID tableId, UUID floorId, String name, BigDecimal minBet, TableState state, Integer playerCount) {
        return new Table(tableId, floorId, name, minBet, state, playerCount);
    }

    public UUID getTableId() {
        return tableId;
    }

    public UUID getFloorId() {
        return floorId;
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
