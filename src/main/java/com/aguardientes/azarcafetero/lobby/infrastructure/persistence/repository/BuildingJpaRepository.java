package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository;

import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.BuildingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BuildingJpaRepository extends JpaRepository<BuildingJpaEntity, UUID> {
}
