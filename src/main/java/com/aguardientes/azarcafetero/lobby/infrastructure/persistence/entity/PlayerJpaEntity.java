package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity;

import com.aguardientes.azarcafetero.lobby.domain.model.PlayerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "players")
public class PlayerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    @Setter
    private UUID id;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private PlayerStatus status;
}