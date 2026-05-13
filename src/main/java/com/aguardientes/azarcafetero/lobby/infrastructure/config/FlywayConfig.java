package com.aguardientes.azarcafetero.lobby.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
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

    /** Forces the JPA EntityManagerFactory to wait for Flyway migrations to complete. */
    @Bean
    public static BeanFactoryPostProcessor flywayEntityManagerDependency() {
        return factory -> {
            BeanDefinition emf = factory.getBeanDefinition("entityManagerFactory");
            String[] existing = emf.getDependsOn();
            String[] updated = existing == null
                    ? new String[]{"flyway"}
                    : java.util.Arrays.copyOf(existing, existing.length + 1);
            if (existing != null) updated[existing.length] = "flyway";
            emf.setDependsOn(updated);
        };
    }
}
