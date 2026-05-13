
package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository;

import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, String> {
    List<TransactionJpaEntity> findByUserIdOrderByCreatedAtDesc(String userId);
}

