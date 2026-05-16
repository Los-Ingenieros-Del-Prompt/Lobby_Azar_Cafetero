package com.aguardientes.azarcafetero.lobby.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
public class FlywayConfig {

    private static final String FLYWAY_BEAN = "flyway";

    @Bean
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas("lobby")
                .createSchemas(true)
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
        return flyway;
    }

    @Bean
    public static BeanFactoryPostProcessor flywayEntityManagerDependency() {
        return factory -> {
            if (factory.containsBeanDefinition(FLYWAY_BEAN)) {
                BeanDefinition emf = factory.getBeanDefinition("entityManagerFactory");
                String[] existing = emf.getDependsOn();
                String[] updated = existing == null
                        ? new String[]{FLYWAY_BEAN}
                        : Arrays.copyOf(existing, existing.length + 1);
                if (existing != null) updated[existing.length] = FLYWAY_BEAN;
                emf.setDependsOn(updated);
            }
        };
    }
}
