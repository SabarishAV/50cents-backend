package io.app._cents.backend.repositories;

import io.app._cents.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User, String> {

    User findByUsername(String username);

}
