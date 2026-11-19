package io.app._cents.backend.health.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
@Tag(name = "Health APIs", description = "Server health related APIs")
public class HealthController {

  @GetMapping()
  @Operation(summary = "Check health", description = "Check server health")
  public Map<String, String> checkHealth() {
    return Map.of("status", "ok", "time", Instant.now().toString());
  }
}
