# Менеджер задач (Java)

[![hexlet-check](https://github.com/VasylP0/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/VasylP0/java-project-99/actions)

## Описание

Менеджер задач — веб-приложение для управления задачами, пользователями,
статусами и метками. Приложение поддерживает аутентификацию,
назначение исполнителей и фильтрацию задач.

Учебный проект Хекслета: https://ru.hexlet.io/programs/java

## Live Demo

[Task Manager on Render](https://java-project-99-caik.onrender.com/welcome)

## Стек технологий

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT
- Hibernate
- PostgreSQL
- H2
- MapStruct
- Swagger / OpenAPI
- Sentry
- Gradle
- JUnit 5

## Требования

- JDK 25 (по умолчанию) или JDK 21
- Git
- Make (для использования команд Makefile)

## Установка

Клонирование репозитория:

```bash
git clone https://github.com/VasylP0/java-project-99.git
cd java-project-99
```

Сборка проекта:

```bash
make setup
```

## Запуск приложения

```bash
make run
```

Для запуска с JDK 21:

```bash
./gradlew bootRun -PjavaVersion=21
```

После запуска приложение доступно по адресу:

http://localhost:8080

## Тестирование

```bash
make test
```

Для запуска тестов с JDK 21:

```bash
./gradlew test -PjavaVersion=21
```

## Документация API

Swagger UI:

http://localhost:8080/swagger-ui/index.html

OpenAPI:

http://localhost:8080/v3/api-docs
