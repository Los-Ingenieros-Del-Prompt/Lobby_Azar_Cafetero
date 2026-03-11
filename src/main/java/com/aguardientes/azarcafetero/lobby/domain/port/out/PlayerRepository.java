package com.aguardientes.azarcafetero.lobby.domain.port.out;

import com.aguardientes.azarcafetero.lobby.domain.model.Player;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {

    Optional<Player> findById(UUID id);

    Optional<Player> findByUsername(String username);

    void save(Player player);

    void saveNew(Player player);
}
