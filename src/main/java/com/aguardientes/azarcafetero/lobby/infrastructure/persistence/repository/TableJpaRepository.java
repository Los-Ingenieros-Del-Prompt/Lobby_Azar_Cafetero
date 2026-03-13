package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository;

import com.aguardientes.azarcafetero.lobby.domain.model.TableState;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.TableJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TableJpaRepository extends JpaRepository<TableJpaEntity, UUID> {
    @Query("SELECT t FROM TableJpaEntity t WHERE t.floor.floorId = :floorId AND t.state = :state")
    List<TableJpaEntity> findByFloorIdAndState(@Param("floorId") UUID floorId, @Param("state") TableState state);
}
