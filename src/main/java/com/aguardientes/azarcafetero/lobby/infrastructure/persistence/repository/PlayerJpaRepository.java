package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository;

import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.PlayerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerJpaRepository extends JpaRepository<PlayerJpaEntity, UUID> {
    Optional<PlayerJpaEntity> findByUsername(String username);
}
