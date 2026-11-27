.PHONY: setup build test run start lint format

setup:
	./gradlew clean build

build:
	./gradlew build

test:
	./gradlew test

run:
	./gradlew bootRun

start:
	./gradlew bootRun

lint:
	./gradlew spotlessCheck

format:
	./gradlew spotlessApply
