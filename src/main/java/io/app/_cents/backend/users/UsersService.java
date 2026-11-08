package io.app._cents.backend.users;

import static io.app._cents.backend.common.services.email.EmailTemplates.getEmailHtml;
import static io.app._cents.backend.common.utils.OtpGenerator.generate6DigitOtp;

import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.exceptions.BadRequestException;
import io.app._cents.backend.common.exceptions.InvalidOtpException;
import io.app._cents.backend.common.exceptions.NotFoundException;
import io.app._cents.backend.common.services.email.EmailService;
import io.app._cents.backend.common.services.redis.RedisService;
import io.app._cents.backend.users.dtos.create.CreateUseRequest;
import io.app._cents.backend.users.dtos.update.UpdateUserRequest;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.types.UserRole;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsersService {

  private static String getChangeEmailKey(String userId) {
    return "otp:reset-mail:" + userId;
  }

  private static String getDeleteAccountKey(String userId) {
    return "otp:delete-acc:" + userId;
  }

  @Autowired private UsersRepository usersRepository;

  @Autowired private EmailService emailService;
  @Autowired private RedisService redisService;

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

  public boolean existsByEmail(String email) {
    return usersRepository.existsByEmail(email);
  }

  public Optional<User> findById(String id) {
    return usersRepository.findById(id);
  }

  public Optional<User> findByEmail(String email) {
    return usersRepository.findByEmail(email);
  }

  public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
    return usersRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
  }

  public User getReferenceById(String userId) {
    return usersRepository.getReferenceById(userId);
  }

  public Optional<User> findUserByGoogleIdOrEmail(String googleId, String email) {
    return usersRepository.findByGoogleId(googleId).or(() -> usersRepository.findByEmail(email));
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

  public ResponseEntity<User> getUser(String userId) {
    User user =
        usersRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  throw new NotFoundException("User");
                });

    user.setGoogleId(null);
    user.setPassword(null);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  public ResponseEntity<Response> changeEmailOtpRequest(String userId, String rawEmail) {
    String email = rawEmail.trim().toLowerCase();

    User user =
        usersRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  throw new NotFoundException("User");
                });

    if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
      throw new BadRequestException(
          "The new email address is the same as your current one. Please enter a different email.");
    }

    boolean emailUserExists = usersRepository.existsByEmail(email);
    if (emailUserExists) {
      throw new BadRequestException("User with this email already exists");
    }

    int otp = generate6DigitOtp();
    emailService.sendHtml(
        email, "OTP to update email", getEmailHtml("OTP to update email", "OTP: " + otp));
    redisService.set(
        getChangeEmailKey(userId), String.valueOf(otp) + ":" + email, Duration.ofMinutes(5));
    return new ResponseEntity<>(
        new Response(true, "OTP send to email successfully - Valid for 5 minutes"), HttpStatus.OK);
  }

  public ResponseEntity<Response> changeEmailOtpVerify(String userId, String otp) {
    User user = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("User"));

    String key = redisService.get(getChangeEmailKey(userId));
    if (key == null) {
      throw new BadRequestException("OTP expired or not requested to change email");
    }
    String[] parts = key.split(":", 2);
    String storedOtp = parts[0];
    String storedEmail = parts[1];

    if (!Objects.equals(storedOtp, otp)) {
      throw new InvalidOtpException();
    }

    user.setEmail(storedEmail);
    usersRepository.save(user);
    redisService.delete(getChangeEmailKey(userId));
    return new ResponseEntity<>(new Response("Email updated successfully"), HttpStatus.OK);
  }

  public ResponseEntity<Response> deleteAccountRequest(String userId, String password) {
    User user = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("User"));
    if (user.getEmail() == null || user.getEmail().isEmpty()) {
      throw new BadRequestException(
          "No email is linked with this account. Update email to delete account");
    }
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
      if (password == null || password.isEmpty()) {
        throw new BadRequestException("Account password is required");
      }
      boolean passwordMatch = encoder.matches(password, user.getPassword());
      if (!passwordMatch) {
        throw new BadRequestException("Incorrect password");
      }
    }
    int otp = generate6DigitOtp();
    emailService.sendHtml(
        user.getEmail(),
        "OTP to delete account",
        getEmailHtml("OTP to delete account", "OTP: " + otp));
    redisService.set(getDeleteAccountKey(userId), String.valueOf(otp), Duration.ofMinutes(5));
    return new ResponseEntity<>(
        new Response(true, "OTP send to email successfully - Valid for 5 minutes"), HttpStatus.OK);
  }

  public ResponseEntity<Response> deleteAccountOtpVerify(String userId, String otp) {
    User user = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("User"));

    String storedOtp = redisService.get(getDeleteAccountKey(userId));
    if (storedOtp == null) {
      throw new BadRequestException("OTP expired or not requested to delete account");
    }

    if (!Objects.equals(storedOtp, otp)) {
      throw new InvalidOtpException();
    }

    usersRepository.delete(user);
    redisService.delete(getDeleteAccountKey(userId));
    return new ResponseEntity<>(new Response("Account deleted successfully"), HttpStatus.OK);
  }
}
