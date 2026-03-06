package com.aguardientes.azarcafetero.lobby.infrastructure.config;

import com.aguardientes.azarcafetero.lobby.domain.port.in.CheckZeroBalanceUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetBuildingStructureUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetPlayerIdentityUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetUpdatedBalanceUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BuildingRepository;
import com.aguardientes.azarcafetero.lobby.domain.port.out.PlayerRepository;
import com.aguardientes.azarcafetero.lobby.domain.service.CheckZeroBalanceService;
import com.aguardientes.azarcafetero.lobby.domain.service.GetBuildingStructureService;
import com.aguardientes.azarcafetero.lobby.domain.service.GetPlayerIdentityService;
import com.aguardientes.azarcafetero.lobby.domain.service.GetUpdatedBalanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public GetBuildingStructureUseCase getBuildingStructureUseCase(BuildingRepository buildingRepository) {
        return new GetBuildingStructureService(buildingRepository);
    }

    @Bean
    public GetPlayerIdentityUseCase getPlayerIdentityUseCase(PlayerRepository playerRepository) {
        return new GetPlayerIdentityService(playerRepository);
    }

    @Bean
    public GetUpdatedBalanceUseCase getUpdatedBalanceUseCase(PlayerRepository playerRepository) {
        return new GetUpdatedBalanceService(playerRepository);
    }

    @Bean
    public CheckZeroBalanceUseCase checkZeroBalanceUseCase(PlayerRepository playerRepository) {
        return new CheckZeroBalanceService(playerRepository);
    }
}
