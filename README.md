# Hatchling API

A knockabout API for testing things that use APIs, like StackHawk.

## Running Hatchling API

This app has been tested with JDK 21. To start it, run:

```shell
$ ./gradlew bootRun
```

The API should be available at `http://localhost:8080/`.
The OAS is available as JSON at `http://localhost:8080/openapi`.
The OAS is available as YAML at `http://localhost:8080/openapi.yaml`.
The Swagger UI is available at `http://localhost:8080/swagger`.
