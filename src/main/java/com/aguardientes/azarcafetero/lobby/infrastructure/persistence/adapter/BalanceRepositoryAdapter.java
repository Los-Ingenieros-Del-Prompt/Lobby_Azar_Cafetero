

package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.adapter;

import com.aguardientes.azarcafetero.lobby.domain.model.Amount;
import com.aguardientes.azarcafetero.lobby.domain.model.Balance;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BalanceRepository;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.BalanceJpaEntity;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository.BalanceJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class BalanceRepositoryAdapter implements BalanceRepository {

    private final BalanceJpaRepository jpaRepository;

    public BalanceRepositoryAdapter(BalanceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Balance> findByUserId(String userId) {
        return jpaRepository.findById(userId).map(e ->
                new Balance(e.getUserId(), new Amount(e.getAmount()), e.getLastBonusDate())
        );
    }

    @Override
    public void save(Balance balance) {
        jpaRepository.save(new BalanceJpaEntity(
                balance.getUserId(),
                balance.getAmount().getValue(),
                balance.getLastBonusDate()
        ));
    }
}

