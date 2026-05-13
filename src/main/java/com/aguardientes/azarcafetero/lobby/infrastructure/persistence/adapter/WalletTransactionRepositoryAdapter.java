
package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.adapter;

import com.aguardientes.azarcafetero.lobby.domain.model.Amount;
import com.aguardientes.azarcafetero.lobby.domain.model.WalletTransaction;
import com.aguardientes.azarcafetero.lobby.domain.port.out.WalletTransactionRepository;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.TransactionJpaEntity;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository.TransactionJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WalletTransactionRepositoryAdapter implements WalletTransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    public WalletTransactionRepositoryAdapter(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(WalletTransaction tx) {
        jpaRepository.save(new TransactionJpaEntity(
                tx.getTransactionId(),
                tx.getUserId(),
                tx.getAmount().getValue(),
                tx.getType(),
                tx.getDescription(),
                tx.getCreatedAt()
        ));
    }

    @Override
    public List<WalletTransaction> findByUserId(String userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(e -> new WalletTransaction(
                        e.getTransactionId(),
                        e.getUserId(),
                        new Amount(e.getAmount()),
                        e.getType(),
                        e.getCreatedAt(),
                        e.getDescription()
                ))
                .collect(Collectors.toList());
    }
}