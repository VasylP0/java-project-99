
.PHONY: setup build test run start

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
