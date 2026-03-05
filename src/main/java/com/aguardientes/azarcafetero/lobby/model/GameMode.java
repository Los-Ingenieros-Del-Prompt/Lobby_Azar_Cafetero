package com.aguardientes.azarcafetero.lobby.model;

// TEMPORARY ENUM, WILL IMPLEMENT ACTUAL LATER


public enum GameMode {
    PARQUES(2, 4),
    BRISCA(2, 2);

    private final int minPlayers;
    private final int maxPlayers;

    GameMode(int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() { return minPlayers; }
    public int getMaxPlayers() { return maxPlayers; }
}
