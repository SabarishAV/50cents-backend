package io.app._cents.backend.seeders;

import io.app._cents.backend.users.UsersRepository;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.types.UserRole;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
@Order(1)
public class UserSeeder implements CommandLineRunner {
  private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  private final UsersRepository usersRepository;

  public UserSeeder(UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  User user =
      User.builder()
          .name("User 1")
          .username("user")
          .email("user@example.com")
          .password(encoder.encode("user"))
          .role(UserRole.USER)
          .build();

  User user2 =
      User.builder()
          .name("User 2")
          .username("user2")
          .email("user2@example.com")
          .password(encoder.encode("user2"))
          .role(UserRole.USER)
          .build();

  User admin =
      User.builder()
          .name("Admin 1")
          .username("admin")
          .email("admin@example.com")
          .password(encoder.encode("admin"))
          .role(UserRole.ADMIN)
          .build();

  @Override
  public void run(String... args) {
    Set<String> desiredUsernames = Set.of("user", "user2", "admin");
    Set<String> existing =
        usersRepository.findAllByUsernameIn(desiredUsernames).stream()
            .map(User::getUsername)
            .collect(Collectors.toSet());

    List<User> toCreate =
        List.of(user, user2, admin).stream()
            .filter(u -> !existing.contains(u.getUsername()))
            .toList();
    if (toCreate.isEmpty()) {
      log.info("Users already seeded, nothing to do");
      return;
    }

    usersRepository.saveAll(toCreate);
    log.info(
        "Seeded {} user(s): {}",
        toCreate.size(),
        toCreate.stream().map(User::getUsername).toList());
  }
}
