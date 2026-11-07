package io.app._cents.backend.users;

import io.app._cents.backend.users.models.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User, String> {

  User findByUsername(String username);

  Optional<User> findByGoogleId(String googleId);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByUsernameOrEmail(String username, String email);

  List<User> findAllByUsernameIn(Set<String> usernames);

  @Query(
      value =
          """
                            SELECT EXISTS (
                                SELECT 1
                                FROM users
                                WHERE username = :username
                                   OR email = :email
                            ) AS result;
                            """,
      nativeQuery = true)
  boolean doesUserExists(@Param("username") String username, @Param("email") String email);
}
