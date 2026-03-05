package com.aguardientes.azarcafetero.lobby.infrastructure.config;

import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.BuildingJpaEntity;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity.FloorJpaEntity;
import com.aguardientes.azarcafetero.lobby.infrastructure.persistence.repository.BuildingJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class DataInitializer {

    private static final UUID DEFAULT_BUILDING_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Bean
    CommandLineRunner initDatabase(BuildingJpaRepository buildingRepository) {
        return args -> {
            if (buildingRepository.findById(DEFAULT_BUILDING_ID).isEmpty()) {
                BuildingJpaEntity building = new BuildingJpaEntity();
                building.setId(DEFAULT_BUILDING_ID);

                FloorJpaEntity floor1 = new FloorJpaEntity();
                floor1.setFloorId(UUID.randomUUID());
                floor1.setName("Parques");
                floor1.setIcon("🎲");
                floor1.setRoute("/parques");
                floor1.setFloorOrder(1);

                FloorJpaEntity floor2 = new FloorJpaEntity();
                floor2.setFloorId(UUID.randomUUID());
                floor2.setName("Brisca");
                floor2.setIcon("🃏");
                floor2.setRoute("/brisca");
                floor2.setFloorOrder(2);

                building.addFloor(floor1);
                building.addFloor(floor2);

                buildingRepository.save(building);
                System.out.println("✅ Building initialized with 2 floors: Parques and Brisca");
            }
        };
    }
}
