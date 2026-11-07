package io.app._cents.backend.users;

import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.exceptions.NotFoundException;
import io.app._cents.backend.users.dtos.create.CreateUseRequest;
import io.app._cents.backend.users.dtos.update.UpdateUserRequest;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.types.UserRole;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsersService {

  @Autowired private UsersRepository usersRepository;

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

  @Transactional
  public ResponseEntity<Response> update(String id, UpdateUserRequest req) {
    User user = usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User"));

    if (req.getName() != null) user.setName(req.getName());
    if (req.getUsername() != null) user.setUsername(req.getUsername());
    if (req.getAvatar() != null) user.setAvatar(req.getAvatar());
    if (req.getBankBalance() != null) user.setBankBalance(req.getBankBalance());

    usersRepository.save(user);
    return new ResponseEntity<>(new Response(true, "User updated suceesfully"), HttpStatus.OK);
  }
}
