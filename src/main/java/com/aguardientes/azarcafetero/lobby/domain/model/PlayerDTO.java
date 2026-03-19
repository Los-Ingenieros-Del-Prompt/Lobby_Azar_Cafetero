package com.aguardientes.azarcafetero.lobby.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class PlayerDTO {
    private UUID id;
    private String username;
    private String displayName;
    private String avatar;
    private BigDecimal balance;

    public PlayerDTO() {
    }

    public PlayerDTO(UUID id, String username, String displayName, String avatar, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
        this.balance = balance;
    }

    public static PlayerDTO from(Player player) {
        return new PlayerDTO(
                player.getId(),
                player.getUsername(),
                player.getDisplayName(),
                player.getAvatar(),
                player.getBalance()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "PlayerDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", balance=" + balance +
                '}';
    }
}
