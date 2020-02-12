FROM maven:3.6.3-jdk-8-slim as builder

WORKDIR /build
COPY . .

RUN mvn clean install spring-boot:repackage --quiet
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG DEPENDENCY=/build/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-cp", "app:app/lib/*", "com.csuvikg.primes.PrimesApplication"]

EXPOSE 8080
