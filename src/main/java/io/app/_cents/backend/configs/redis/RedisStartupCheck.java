package io.app._cents.backend.configs.redis;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStartupCheck implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(RedisStartupCheck.class);

  private final StringRedisTemplate redis;

  public RedisStartupCheck(StringRedisTemplate redis) {
    this.redis = redis;
  }

  @Override
  public void run(String... args) {
    redis.opsForValue().set("health:test", "ok", Duration.ofSeconds(30));
    log.info("Redis check: {}", redis.opsForValue().get("health:test"));
  }
}
