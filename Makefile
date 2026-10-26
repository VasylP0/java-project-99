.PHONY: setup build test run

setup:
	./gradlew clean build

build:
	./gradlew build

test:
	./gradlew test

run:
	./gradlew bootRun
