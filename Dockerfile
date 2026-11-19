# syntax=docker/dockerfile:1
# Build context: the `backend/` directory.

# ---------- build stage ----------
FROM gradle:9.7.1-jdk17 AS build
WORKDIR /workspace

COPY settings.gradle.kts build.gradle.kts gradle.properties* ./
RUN gradle --no-daemon --quiet dependencies

COPY src src
RUN gradle --no-daemon bootJar -x installGitHooks

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

RUN groupadd --system --gid 1001 app \
 && useradd --system --uid 1001 --gid app --create-home app

COPY --from=build --chown=app:app /workspace/build/libs/*.jar /app/app.jar

USER app

# Small-memory defaults; override at runtime by setting JAVA_OPTS.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=65 -XX:+UseSerialGC"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080}"]