package com.aguardientes.azarcafetero.lobby.domain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;

    @Enumerated(EnumType.STRING)
    private PlayerStatus status;



}
