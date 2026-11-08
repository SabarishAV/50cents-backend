package io.app._cents.backend.common.services.redis;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

  private final StringRedisTemplate redis;

  public RedisService(StringRedisTemplate redis) {
    this.redis = redis;
  }

  // Save a value that expires after the given time
  public void set(String key, String value, Duration ttl) {
    redis.opsForValue().set(key, value, ttl);
  }

  // Save a value that never expires
  public void set(String key, String value) {
    redis.opsForValue().set(key, value);
  }

  // Returns null if the key doesn't exist or has expired
  public String get(String key) {
    return redis.opsForValue().get(key);
  }

  // Returns true if the key existed and was deleted
  public boolean delete(String key) {
    return Boolean.TRUE.equals(redis.delete(key));
  }

  public boolean exists(String key) {
    return Boolean.TRUE.equals(redis.hasKey(key));
  }
}
