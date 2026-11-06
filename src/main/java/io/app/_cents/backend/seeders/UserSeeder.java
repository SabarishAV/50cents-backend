package io.app._cents.backend.seeders;

import io.app._cents.backend.users.UsersRepository;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.types.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("seed")
public class UserSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private final UsersRepository usersRepository;

    public UserSeeder(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    User user = User.builder()
            .name("User 1")
            .username("user")
            .email("user@example.com")
            .password(encoder.encode("user"))
            .role(UserRole.USER)
            .build();

    User admin = User.builder()
            .name("Admin 1")
            .username("admin")
            .email("admin@example.com")
            .password(encoder.encode("admin"))
            .role(UserRole.ADMIN)
            .build();

    @Override
    public void run(String... args) {
        usersRepository.saveAll(
                List.of(user, admin)
        );
        log.info("Users seeded successfully");
    }
}
