# java8-spring-boot-seed

## Requirements

- [JDK 8](https://adoptium.net/es/temurin/releases)

## Getting started

1. Clone the repository.
2. Download dependencies via `./mvnw install` (or use your IDE's auto-import).
3. Run unit tests with `./mvnw test` or `./mvnw verify` (to validate rules with JaCoCo).
4. Start application with `SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run`.

## Run integration tests with embedded Karate

`./mvnw test -Dtest=KarateRunner -DargLine=-Dkarate.env={ENVIRONMENT}`

## Kickstart a project

This seed was created as a reference point to bootstrap new services. There are a number of things to do in order to
accomplish that:

1. Change `artifactId`, `name` and `description` parameters in `pom.xml`.
2. Rename root directory `java8-spring-boot-seed` to match your brand new `artifactId`.
3. Change this README's title.
4. Configure `cicd.yml` with Sonar's project key and login token.
5. (If this is an API) Complete the steps described in the "Kickstart an API" and then continue with the rest.
6. (If this is a BFF) Complete the steps described in the "Kickstart a BFF" and then continue with the rest.
7. Rename package `seed` to something that better describes your service. Remember to update `CodingRulesTest.java`,
   `Config.java` and `TestConfig.java` (they need to scan the root package to do their job). Remember to change whatever
   paths that make reference to package `seed` in `pom.xml` file.
8. Change Spring's default application name in `application.yml`.
9. Delete `.git` directory.
10. Create new project in your remote repository (Github, GitLab, etc.)
11. Follow the instructions to push your local code to the new project (`git init...`). Before pushing, you may as well
    delete this Kickstart section before committing (it won't be needed any longer).

### Kickstart an API

Since you are trying to setup a new API, it is necessary to get rid of Spring Security and Keycloak. In order to do
that, follow these steps:

1. Delete `AccessControlInterceptor` instance from the `AppConfig` class.
2. Delete `AuthorizationRoleInterceptor` instance from the `AppConfig` class.
3. Delete the directories `src/main/java/ar/com/itau/seed/config/security` and
   `src/main/java/ar/com/itau/seed/config/security`.
4. Delete `SecurityHeaders` inner class and attributes `channelId` and `authRoleInterceptorEnabled` from `Config`
   and `TestConfig` classes. `security-headers` section, `channel-id` and `auth-role-interceptor-enabled` in
   `application.yml` can be safely removed after that.
5. `UserRepository` interface (and its corresponding Adapter) can be deleted depending on whether they are needed or
   not.
6. Delete `TestSecurityConfig.class` from the `@Import` annotation in controller's tests.
7. Delete the file `src.test.ar.com.itau.seed.config.TestSecurityConfig`.
8. Delete Keycloak configuration from `application.yml` (inside both `main` and `test`).
9. Delete artifacts `spring-boot-starter-security`, `keycloak-spring-boot-starter` and `keycloak-adapter-bom`
   (dependencyManagement) from `pom.xml`.
10. Update Karate (Cucumber) features to remove any reference to authorized requests.
11. Remove `base-bff` and `base-error` from `spring.application.name` in `application.yml`.`
12. Update `server.servlet.context-path` to leave the `api` or `acl` prefix (remove the `bff` prefix) in
    `application.yml`. Yoy should also change the property `prefix` used to build the error codes.

### Kickstart a BFF

1. Remove `base-api` from `spring.application.name` in `application.yml`.
2. Update `server.servlet.context-path` to leave the `bff` prefix (remove the `api` prefix) in `application.yml`. Yoy
   should also change the property `prefix` used to build the error codes.

## Package architecture

This project has been designed taking the principles of clean architecture into account. More specifically it uses
a variety known as Hexagonal Architecture (also known as "Ports and Adapter"). More information can be found in the
resources linked below:

- [Clean Architecture by Bob Martin](https://blog.cleancoder.com/uncle-bob/2011/11/22/Clean-Architecture.html)
- [The Clean Architecture by Bob Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Clean Micro-Service Architecture by Bob Martin](https://blog.cleancoder.com/uncle-bob/2014/10/01/CleanMicroserviceArchitecture.html)
- [Buckpal: Example implementation of a Hexagonal Architecture](https://github.com/thombergs/buckpal)

## Swagger

- Swagger UI at [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)
- OAPI (v3) definition at [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)

## Health check probe

`curl -fsS http://localhost:8080/actuator/health`
