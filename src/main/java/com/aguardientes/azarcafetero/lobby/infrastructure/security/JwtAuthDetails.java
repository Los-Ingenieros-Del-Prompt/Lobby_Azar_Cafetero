package com.aguardientes.azarcafetero.lobby.infrastructure.security;

public class JwtAuthDetails {

    private final String name;
    private final String avatarUrl;

    public JwtAuthDetails(String name, String avatarUrl) {
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
}
