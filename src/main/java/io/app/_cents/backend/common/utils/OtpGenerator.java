package io.app._cents.backend.common.utils;

import java.security.SecureRandom;

public class OtpGenerator {

  private static final SecureRandom RANDOM = new SecureRandom();

  // Returns a 6-digit string like "483920" or "007215"
  public static int generate6DigitOtp() {
    return 100000 + RANDOM.nextInt(900000);
  }
}
