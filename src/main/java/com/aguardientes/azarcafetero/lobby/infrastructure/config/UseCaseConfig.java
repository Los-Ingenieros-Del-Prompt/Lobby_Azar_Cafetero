package com.aguardientes.azarcafetero.lobby.infrastructure.config;

import com.aguardientes.azarcafetero.lobby.domain.port.in.GetBuildingStructureUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BuildingRepository;
import com.aguardientes.azarcafetero.lobby.domain.service.GetBuildingStructureService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public GetBuildingStructureUseCase getBuildingStructureUseCase(BuildingRepository buildingRepository) {
        return new GetBuildingStructureService(buildingRepository);
    }
}
