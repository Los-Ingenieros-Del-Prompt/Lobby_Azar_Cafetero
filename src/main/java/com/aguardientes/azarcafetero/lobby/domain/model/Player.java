package com.aguardientes.azarcafetero.lobby.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Player {

    private final UUID id;
    private final String username;
    private final String avatar;
    private BigDecimal balance;
    private PlayerStatus status;

    public Player(UUID id, String username, String avatar, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.balance = balance;
        this.status = PlayerStatus.OFFLINE;
    }

    public static Player create(UUID id, String username) {
        return new Player(id, username, null, BigDecimal.ZERO);
    }

    public static Player from(UUID id, String username, String avatar, BigDecimal balance) {
        return new Player(id, username, avatar, balance);
    }

    public void goOnline() {
        if (status != PlayerStatus.OFFLINE) {
            throw new IllegalStateException("Already online");
        }
        status = PlayerStatus.ONLINE;
    }

    public void enterRoom() {
        if (status != PlayerStatus.ONLINE) {
            throw new IllegalStateException("Must be online first");
        }
        status = PlayerStatus.IN_ROOM;
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getAvatar() { return avatar; }
    public BigDecimal getBalance() { return balance; }
    public PlayerStatus getStatus() { return status; }

}
