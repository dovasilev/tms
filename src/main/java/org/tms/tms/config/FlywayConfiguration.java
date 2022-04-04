package org.tms.tms.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.val;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfiguration {

    @Autowired
    public FlywayConfiguration(@Autowired DataSource dataSource,
                               @Value("${spring.flyway.baseline-on-migrate}") Boolean isMigration) {
        Flyway.configure().initSql("SET search_path = public").baselineOnMigrate(isMigration).dataSource(dataSource).load().migrate();
    }
}