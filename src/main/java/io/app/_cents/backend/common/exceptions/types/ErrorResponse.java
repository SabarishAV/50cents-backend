package io.app._cents.backend.common.exceptions.types;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean success,
        int statusCode,
        String code,
        String message,
        Map<String, String> fields,
        String path,
        Instant timestamp,
        String traceId
) {}
