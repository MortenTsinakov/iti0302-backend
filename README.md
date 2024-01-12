## OneLouder

#### Introduction

A music app designed for enthusiasts who want a straightforward way to manage their music preferences.

#### Author

- Morten Tšinakov

#### Technologies Used

- Java 17+
- Spring Boot 3.1.3
- PostgreSQL 16.0
- MapStruct 1.5.5
- Spring Security 6.2.0
- Liquibase 4.25.0
- Lombok 1.18.30
- Jjwt 0.11.5
- OpenApi 2.1.0

#### Setting up the development environment

1. Clone the project
2. Create a docker compose file for the database: <br>
```yaml
services:
  postgres:
    container_name: postgres_container
    image: postgres
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=docker
    volumes:
      - ./postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
```
3. Create an application.properties file in src/main/resources: <br>
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=docker
spring.datasource.driver-class-name=org.postgresql.Driver

spring.liquibase.change-log=classpath:/db/changelog/changelog-master.xml

spring.jpa.hibernate.ddl-auto=update

external.api.key = <external_api_key>
jwt.secret.key = <jwt_secret_key>
```
4. Run 'docker compose up' command from where the compose file for database is located
5. Run the project from IntelliJ or with './gradlew bootRun' command

PS! You have to add to application.properties:
1. Last.fm API key (can be created on last.fm website).
2. Secret JWT key
