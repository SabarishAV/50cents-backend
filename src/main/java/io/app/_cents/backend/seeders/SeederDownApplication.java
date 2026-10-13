package io.app._cents.backend.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("seed-down")
@EnableAutoConfiguration
public class SeederDownApplication {

    private static final Logger log = LoggerFactory.getLogger(SeederDownApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SeederDownApplication.class)
                .web(WebApplicationType.NONE)
                .profiles("seed-down")
                .properties("spring.jpa.hibernate.ddl-auto=none")
                .run(args);
        System.exit(SpringApplication.exit(context));
    }

    @Bean
    CommandLineRunner dropEverything(JdbcTemplate jdbc, @Value("${spring.datasource.url}") String url) {
        return args -> {
            log.warn("Dropping everything in {}", url);
            jdbc.execute("DROP SCHEMA public CASCADE");
            jdbc.execute("CREATE SCHEMA public");
            log.info("Database wiped");
        };
    }
}
