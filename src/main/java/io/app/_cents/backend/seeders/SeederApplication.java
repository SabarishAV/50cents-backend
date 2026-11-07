package io.app._cents.backend.seeders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("seed")
@EnableAutoConfiguration
@EntityScan("io.app._cents.backend")
@EnableJpaRepositories("io.app._cents.backend")
@Import({CategorySeeder.class, UserSeeder.class, PaymentRecordSeeder.class})
public class SeederApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context =
        new SpringApplicationBuilder(SeederApplication.class)
            .web(WebApplicationType.NONE)
            .profiles("seed")
            .run(args);
    System.exit(SpringApplication.exit(context));
  }
}
