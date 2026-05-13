
package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository;

import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.BalanceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceJpaRepository extends JpaRepository<BalanceJpaEntity, String> {}

