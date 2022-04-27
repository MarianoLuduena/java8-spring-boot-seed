# java8-spring-boot-seed

## Requirements

- [JDK 8](https://adoptium.net/es/temurin/releases)

## Getting started

1. Clone the repository.
2. Download dependencies via `./mvnw install` (or use your IDE's auto-import).
3. Run tests with `./mvnw test` or `./mvnw verify` (to validate rules with JaCoCo).
4. Start application with `SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run`.

## Kickstart a project

This seed was created as a reference point to bootstrap new services. There are a number of things to do in order to 
accomplish that:

1. Change `artifactId`, `name` and `description` parameters in `pom.xml`.
2. Rename root directory `java8-spring-boot-seed` to match your brand new `artifactId`.
3. Change this README's title.
4. Configure `cicd.yml` with Sonar's project key and login token.
5. Rename package `seed` to something that better describes your service. Remember to update `CodingRulesTest.java` and 
`Config.java` (they need to scan the root package to do their job).
6. Change Spring's default application name in `application.yml`.
7. Delete `.git` directory.
8. Create new project in your remote repository (Github, GitLab, etc.)
9. Follow the instructions to push your local code to the new project (`git init...`). Before pushing, you may as well 
delete this Kickstart section before committing (it won't be needed any longer).

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
