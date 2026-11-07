package io.app._cents.backend.configs.jwt;

import io.app._cents.backend.users.UsersRepository;
import io.app._cents.backend.users.models.User;
import io.app._cents.backend.users.models.others.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

  @Autowired private UsersRepository usersRepository;

  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    User user =
        usersRepository
            .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

    return new UserPrincipal(user);
  }
}
