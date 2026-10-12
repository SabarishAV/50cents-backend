package io.app._cents.backend.auth;

import io.app._cents.backend.auth.dtos.login.LoginUserRequest;
import io.app._cents.backend.auth.dtos.login.LoginUserResponse;
import io.app._cents.backend.auth.dtos.registration.RegisterUserRequest;
import io.app._cents.backend.auth.dtos.registration.RegisterUserResponse;
import io.app._cents.backend.auth.exceptions.InvalidCredentialsException;
import io.app._cents.backend.auth.exceptions.UserAlreadyExistsException;
import io.app._cents.backend.auth.exceptions.UserNotFoundException;
import io.app._cents.backend.configs.jwt.JwtService;
import io.app._cents.backend.users.UsersService;
import io.app._cents.backend.users.dtos.create.CreateUseRequest;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.models.others.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired private JwtService jwtService;
    @Autowired private UsersService usersService;

    public ResponseEntity<RegisterUserResponse> registerUser(RegisterUserRequest registerUser) {
            boolean doesUserExists = usersService.checkUserExists(
                    registerUser.getUsername(),
                    registerUser.getEmail()
            );
            if(doesUserExists){
                throw new UserAlreadyExistsException("User already exists by the email or username");
            }

            usersService.createUser(
                    new CreateUseRequest(
                            registerUser.getName(),
                            registerUser.getUsername(),
                            registerUser.getEmail(),
                            registerUser.getPassword(),
                            registerUser.getAvatar()
                    )
            );

            return new ResponseEntity<>(new RegisterUserResponse(
                    true,
                    "User created successfully"
            ), HttpStatus.CREATED);

    }

    public ResponseEntity<LoginUserResponse> login(LoginUserRequest loginUser) {

        User user;
        try {
            Authentication authentication =authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsernameOrEmail(), loginUser.getPassword()));
            user = ((UserPrincipal) authentication.getPrincipal()).getUser();
        } catch(Exception e) {
            logger.error(e.getMessage());
            throw new InvalidCredentialsException();
        }


            String token =
                    jwtService.generateToken(
                            user.getId(),
                            user.getName(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getAvatar()
                    );
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalStateException("Token not generated");
            }
            return new ResponseEntity<>(new LoginUserResponse(
                    true,
                    token
            ), HttpStatus.OK);
    }

}
