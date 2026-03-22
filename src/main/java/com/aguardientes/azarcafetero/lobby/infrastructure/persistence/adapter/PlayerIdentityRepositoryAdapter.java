package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.adapter;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.PlayerJpaEntity;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository.PlayerJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerIdentityRepositoryAdapter implements PlayerRepository {

    private final PlayerJpaRepository jpaRepository;

    public PlayerIdentityRepositoryAdapter(PlayerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Player> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Player> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public void save(Player player) {
        PlayerJpaEntity entity = toEntity(player);
        jpaRepository.save(entity);
    }

    @Override
    public void saveNew(Player player) {
        PlayerJpaEntity entity = toEntity(player);
        entity.markAsNew();
        jpaRepository.save(entity);
    }

    private PlayerJpaEntity toEntity(Player player) {
        PlayerJpaEntity entity = new PlayerJpaEntity();
        entity.setId(player.getId());
        entity.setUsername(player.getUsername());
        entity.setDisplayName(player.getDisplayName());
        entity.setAvatar(player.getAvatar());
        entity.setBalance(player.getBalance());
        entity.setStatus(player.getStatus());
        return entity;
    }

    private Player toDomain(PlayerJpaEntity entity) {
        return Player.from(entity.getId(), entity.getUsername(), entity.getDisplayName(), entity.getAvatar(), entity.getBalance());
    }
}
