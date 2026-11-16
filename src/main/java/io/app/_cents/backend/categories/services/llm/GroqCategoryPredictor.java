package io.app._cents.backend.categories.services.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.payments.models.enums.TransactionType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GroqCategoryPredictor {

  private static final Logger log = LoggerFactory.getLogger(GroqCategoryPredictor.class);

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final String apiKey;
  private final String baseUrl;
  private final String model;
  private final Duration timeout;

  public GroqCategoryPredictor(
      @Value("${groq.apiKey:}") String apiKey,
      @Value("${groq.baseUrl:https://api.groq.com/openai/v1}") String baseUrl,
      @Value("${groq.model:llama-3.1-8b-instant}") String model,
      @Value("${groq.timeout-ms:1500}") long timeoutMs) {
    this.apiKey = apiKey == null ? "" : apiKey.trim();
    this.baseUrl = baseUrl == null ? "" : baseUrl.trim();
    this.model = model == null ? "" : model.trim();
    this.timeout = Duration.ofMillis(Math.max(100, timeoutMs));
  }

  public boolean isEnabled() {
    return !apiKey.isBlank() && !baseUrl.isBlank() && !model.isBlank();
  }

  public Optional<String> predict(
      String recipient,
      String note,
      TransactionType transactionType,
      Collection<Category> categories,
      Set<String> allowedCodes) {
    if (!isEnabled() || allowedCodes == null || allowedCodes.isEmpty()) {
      return Optional.empty();
    }

    try {
      String prompt = buildPrompt(recipient, note, transactionType, categories, allowedCodes);

      Map<String, Object> body = new LinkedHashMap<>();
      body.put("model", model);
      body.put("temperature", 0);
      body.put("max_tokens", 20);
      body.put(
          "messages",
          List.of(
              Map.of(
                  "role",
                  "system",
                  "content",
                  "You classify payment transactions into a category. "
                      + "Reply with ONLY the category code from the allowed list. "
                      + "No explanation, no punctuation, no extra text."),
              Map.of("role", "user", "content", prompt)));

      String json = objectMapper.writeValueAsString(body);

      HttpClient client = HttpClient.newBuilder().connectTimeout(timeout).build();

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl.replaceAll("/+$", "") + "/chat/completions"))
              .timeout(timeout)
              .header("Authorization", "Bearer " + apiKey)
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(json))
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() / 100 != 2) {
        log.debug("Groq prediction failed with status {}", response.statusCode());
        return Optional.empty();
      }

      String content = extractContent(response.body());
      String code = normalizeCode(content);
      if (code != null && allowedCodes.contains(code)) {
        return Optional.of(code);
      }
      return Optional.empty();
    } catch (Exception e) {
      log.debug("Groq prediction unavailable, falling back to keywords: {}", e.toString());
      return Optional.empty();
    }
  }

  private String buildPrompt(
      String recipient,
      String note,
      TransactionType transactionType,
      Collection<Category> categories,
      Set<String> allowedCodes) {
    String options =
        categories == null
            ? String.join(", ", allowedCodes)
            : categories.stream()
                .filter(c -> c != null && c.getCode() != null && allowedCodes.contains(c.getCode()))
                .map(c -> c.getCode() + " (" + safe(c.getName()) + ")")
                .distinct()
                .collect(Collectors.joining(", "));

    return "Transaction type: "
        + (transactionType == null ? "UNKNOWN" : transactionType.name())
        + "\nRecipient: "
        + safe(recipient)
        + "\nNote: "
        + safe(note)
        + "\nAllowed category codes: "
        + options
        + "\nReturn the single best category code.";
  }

  private String extractContent(String responseBody) throws Exception {
    JsonNode root = objectMapper.readTree(responseBody);
    JsonNode choices = root.path("choices");
    if (!choices.isArray() || choices.isEmpty()) {
      return null;
    }
    return choices.get(0).path("message").path("content").asText(null);
  }

  private static String normalizeCode(String content) {
    if (content == null) {
      return null;
    }
    String s = content.trim();
    if (s.isEmpty()) {
      return null;
    }
    // take first token-like code in case model adds extras
    s = s.replaceAll("[`\"']", "").trim();
    String[] parts = s.split("\\s+");
    String first = parts.length > 0 ? parts[0] : s;
    first = first.replaceAll("[^A-Z0-9_]", "");
    return first.isEmpty() ? null : first;
  }

  private static String safe(String s) {
    if (s == null) {
      return "";
    }
    String v = s.replaceAll("\\s+", " ").trim();
    return v.length() > 300 ? v.substring(0, 300) : v;
  }
}
