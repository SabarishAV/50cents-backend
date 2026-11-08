package io.app._cents.backend.auth.services;

import static io.app._cents.backend.common.services.email.EmailTemplates.getEmailHtml;
import static io.app._cents.backend.common.utils.OtpGenerator.generate6DigitOtp;

import io.app._cents.backend.auth.dtos.login.LoginUserRequest;
import io.app._cents.backend.auth.dtos.login.LoginUserResponse;
import io.app._cents.backend.auth.dtos.otp.VerifyOtpRequest;
import io.app._cents.backend.auth.dtos.password.ChangePasswordRequest;
import io.app._cents.backend.auth.dtos.password.ForgotPasswordRequest;
import io.app._cents.backend.auth.dtos.password.ForgotPasswordVerifyRequest;
import io.app._cents.backend.auth.dtos.registration.RegisterUserRequest;
import io.app._cents.backend.auth.exceptions.InvalidCredentialsException;
import io.app._cents.backend.auth.exceptions.UserAlreadyExistsException;
import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.exceptions.BadRequestException;
import io.app._cents.backend.common.exceptions.InvalidOtpException;
import io.app._cents.backend.common.exceptions.NotFoundException;
import io.app._cents.backend.common.services.email.EmailService;
import io.app._cents.backend.common.services.redis.RedisService;
import io.app._cents.backend.configs.jwt.JwtService;
import io.app._cents.backend.users.UsersService;
import io.app._cents.backend.users.dtos.create.CreateUseRequest;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.models.others.UserPrincipal;
import java.time.Duration;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

  @Autowired AuthenticationManager authenticationManager;

  private static final String LOGIN_OTP_KEY_PREFIX = "otp:login:";

  private static String getForgotPasswordKey(String userId) {
    return "otp:forgot-pass:" + userId;
  }

  @Autowired private JwtService jwtService;
  @Autowired private UsersService usersService;
  @Autowired private EmailService emailService;
  @Autowired private RedisService redisService;

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  public ResponseEntity<Response> registerUser(RegisterUserRequest registerUser) {
    boolean doesUserExists =
        usersService.checkUserExists(registerUser.getUsername(), registerUser.getEmail());
    if (doesUserExists) {
      throw new UserAlreadyExistsException("User already exists by the email or username");
    }

    usersService.createUser(
        new CreateUseRequest(
            registerUser.getName(),
            registerUser.getUsername(),
            registerUser.getEmail(),
            registerUser.getPassword(),
            registerUser.getAvatar()));

    return new ResponseEntity<>(
        new Response(true, "User created successfully"), HttpStatus.CREATED);
  }

  public ResponseEntity<LoginUserResponse> login(LoginUserRequest loginUser) {

    User existingUser =
        usersService
            .findByUsernameOrEmail(loginUser.getUsernameOrEmail())
            .orElseThrow(
                () -> {
                  throw new NotFoundException("User");
                });
    if (existingUser.getPassword() == null || existingUser.getPassword().isBlank()) {
      throw new BadRequestException("Password is not set for this user");
    }

    User user;
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginUser.getUsernameOrEmail(), loginUser.getPassword()));
      user = ((UserPrincipal) authentication.getPrincipal()).getUser();
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new InvalidCredentialsException();
    }

    String token =
        jwtService.generateToken(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getAvatar(),
            user.getRole());
    if (token == null || token.trim().isEmpty()) {
      throw new IllegalStateException("Token not generated");
    }
    return new ResponseEntity<>(new LoginUserResponse(true, token), HttpStatus.OK);
  }

  public ResponseEntity<Response> emailOtpRequest(String email) {
    if (!usersService.existsByEmail(email)) {
      throw new NotFoundException("User");
    }

    int otp = generate6DigitOtp();
    emailService.sendHtml(email, "OTP for login", getEmailHtml("OTP for login", "OTP: " + otp));
    redisService.set(LOGIN_OTP_KEY_PREFIX + email, String.valueOf(otp), Duration.ofMinutes(5));

    return new ResponseEntity<>(
        new Response(true, "OTP send successfully. Valid for 5 minutes"), HttpStatus.OK);
  }

  public ResponseEntity<LoginUserResponse> emailOtpVerify(VerifyOtpRequest req) {
    User user =
        usersService
            .findByEmail(req.getEmail())
            .orElseThrow(
                () -> {
                  throw new NotFoundException("User");
                });

    String otp = redisService.get(LOGIN_OTP_KEY_PREFIX + req.getEmail());
    if (otp == null) {
      throw new BadRequestException("OTP expired");
    }
    if (!Objects.equals(otp, req.getOtp())) {
      throw new BadRequestException("Incorrect OTP");
    }

    redisService.delete(LOGIN_OTP_KEY_PREFIX + req.getEmail());

    String token =
        jwtService.generateToken(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getAvatar(),
            user.getRole());
    if (token == null || token.trim().isEmpty()) {
      throw new IllegalStateException("Token not generated");
    }
    return new ResponseEntity<>(new LoginUserResponse(true, token), HttpStatus.OK);
  }

  public ResponseEntity<Response> changePassword(String userId, ChangePasswordRequest req) {
    User user = usersService.findById(userId).orElseThrow(() -> new NotFoundException("User"));
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
      if (req.getCurrentPassword() == null || req.getCurrentPassword().isEmpty()) {
        throw new BadRequestException("Account password is required");
      }
      boolean passwordMatch = encoder.matches(req.getCurrentPassword(), user.getPassword());
      if (!passwordMatch) {
        throw new BadRequestException("Incorrect password");
      }
    }
    user.setPassword(encoder.encode(req.getNewPassword()));
    usersService.save(user);
    return new ResponseEntity<>(new Response("Password updated successfully"), HttpStatus.OK);
  }

  public ResponseEntity<Response> forgotPasswordRequest(ForgotPasswordRequest req) {
    User user =
        usersService
            .findByUsernameOrEmail(req.getIdentifier())
            .orElseThrow(() -> new NotFoundException("User"));

    if (user.getEmail() == null || user.getEmail().isEmpty()) {
      throw new BadRequestException("No email is linked with this account");
    }

    int otp = generate6DigitOtp();
    emailService.sendHtml(
        user.getEmail(),
        "OTP to reset account password",
        getEmailHtml("OTP to reset account password", "OTP: " + otp));
    redisService.set(
        getForgotPasswordKey(user.getId()), String.valueOf(otp), Duration.ofMinutes(5));
    return new ResponseEntity<>(
        new Response(true, "OTP send to email successfully - Valid for 5 minutes"), HttpStatus.OK);
  }

  public ResponseEntity<Response> forgotPasswordVerify(ForgotPasswordVerifyRequest req) {
    User user =
        usersService
            .findByUsernameOrEmail(req.getIdentifier())
            .orElseThrow(() -> new NotFoundException("User"));

    String storedOtp = redisService.get(getForgotPasswordKey(user.getId()));
    if (storedOtp == null) {
      throw new BadRequestException("OTP expired or not requested to change email");
    }
    if (!Objects.equals(storedOtp, req.getOtp())) {
      throw new InvalidOtpException();
    }
    user.setPassword(encoder.encode(req.getPassword()));
    usersService.save(user);
    redisService.delete(getForgotPasswordKey(user.getId()));
    return new ResponseEntity<>(new Response("Password updated successfully"), HttpStatus.OK);
  }
}
