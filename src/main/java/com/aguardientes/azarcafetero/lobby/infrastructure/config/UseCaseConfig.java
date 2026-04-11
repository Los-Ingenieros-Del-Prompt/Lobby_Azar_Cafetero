package com.aguardientes.azarcafetero.lobby.infrastructure.config;

import com.aguardientes.azarcafetero.lobby.domain.port.in.*;
import com.aguardientes.azarcafetero.lobby.domain.port.out.*;
import com.aguardientes.azarcafetero.lobby.domain.service.*;
import com.aguardientes.azarcafetero.lobby.infrastructure.client.WalletClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public WalletClient walletClient(@Value("${wallet.service.url}") String walletServiceUrl) {
        return new WalletClient(walletServiceUrl);
    }

    @Bean
    public GetPlayerIdentityUseCase getPlayerIdentityUseCase(PlayerRepository playerRepository,
                                                             WalletClient walletClient) {
        return new GetPlayerIdentityService(playerRepository, walletClient);
    }

    @Bean
    public GetUpdatedBalanceUseCase getUpdatedBalanceUseCase(PlayerRepository playerRepository) {
        return new GetUpdatedBalanceService(playerRepository);
    }

    @Bean
    public CheckZeroBalanceUseCase checkZeroBalanceUseCase(PlayerRepository playerRepository) {
        return new CheckZeroBalanceService(playerRepository);
    }

    @Bean
    public GetBuildingStructureUseCase getBuildingStructureUseCase(BuildingRepository buildingRepository) {
        return new GetBuildingStructureService(buildingRepository);
    }

    @Bean
    public GetFloorTablesUseCase getFloorTablesUseCase(TableRepository tableRepository) {
        return new GetFloorTablesService(tableRepository);
    }

    @Bean
    public GetPlayerByIdUseCase getPlayerByIdUseCase(PlayerRepository playerRepository) {
        return new GetPlayerByIdService(playerRepository);
    }
}