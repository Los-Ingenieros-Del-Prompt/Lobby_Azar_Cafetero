package com.aguardientes.azarcafetero.lobby.domain.model;

import java.math.BigDecimal;

public class PlayerIdentityDTO {

    private String name;
    private String avatar;
    private BigDecimal balance;

    public PlayerIdentityDTO(String name, String avatar, BigDecimal balance){
        this.name = name;
        this.avatar = avatar;
        this.balance = balance;
    }

    public String getName() {return name;}
    public String getAvatar() { return avatar;}
    public BigDecimal getBalance() { return balance;}
}
