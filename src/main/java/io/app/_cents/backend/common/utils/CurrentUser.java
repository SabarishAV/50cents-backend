package io.app._cents.backend.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {
  public static String id() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (String) auth.getDetails();
  }
}
