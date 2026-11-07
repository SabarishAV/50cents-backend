package io.app._cents.backend.users;

import io.app._cents.backend.users.dtos.create.CreateUseRequest;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.types.UserRole;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

  @Autowired UsersRepository usersRepository;

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  public boolean checkUserExists(String username, String email) {
    return usersRepository.doesUserExists(username, email);
  }

  public User save(User user) {
    return usersRepository.save(user);
  }

  public boolean existsByUsername(String username) {
    return usersRepository.existsByUsername(username);
  }

  public User createUser(CreateUseRequest reqUser) {
    User user =
        User.builder()
            .name(reqUser.getName())
            .username(reqUser.getUsername())
            .email(reqUser.getEmail())
            .password(encoder.encode(reqUser.getPassword()))
            .avatar(reqUser.getAvatar())
            .role(UserRole.USER)
            .build();
    return usersRepository.save(user);
  }

  public User getReferenceById(String userId) {
    return usersRepository.getReferenceById(userId);
  }

  public Optional<User> findUserByGoogleIdOrEmail(String googleId, String email) {
    return usersRepository.findByGoogleId(googleId).or(() -> usersRepository.findByEmail(email));
  }
}
