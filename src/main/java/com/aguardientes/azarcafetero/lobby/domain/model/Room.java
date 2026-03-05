package com.aguardientes.azarcafetero.lobby.domain.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @ManyToMany
    private List<Player> players = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private RoomStatus status;
}