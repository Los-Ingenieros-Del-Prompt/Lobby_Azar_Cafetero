package com.aguardientes.azarcafetero.lobby.domain.model;

import lombok.Getter;

import java.util.UUID;

public class Player {

    private final UUID id;
    private final String username;
    private PlayerStatus status;

    public Player(UUID id, String username) {
        this.id = id;
        this.username = username;
        this.status = PlayerStatus.OFFLINE;
    }

    public static Player create(UUID id, String username) {
        return new Player(id, username);
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
    public PlayerStatus getStatus() { return status; }

}
