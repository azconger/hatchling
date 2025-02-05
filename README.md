# Hatchling API

A knockabout API for testing things that use APIs, like StackHawk.

## Running Hatchling API

This app has been tested with JDK 21. To start it for testing with Gradle, run:

```shell
$ ./gradlew bootRun
```

To use Docker, run:

```shell
$ docker compose up [--detach]
```

Notes:

* Debug logging is turned on by default from the src/main/resources/application.yml file.
* The API should be available at `http://localhost:8080/`.
* The OAS is available as JSON at `http://localhost:8080/openapi`.
* The OAS is available as YAML at `http://localhost:8080/openapi.yaml`.
* The Swagger UI is available at `http://localhost:8080/swagger`.

## Testing with StackHawk

> NOTE: You may need to update the `stackhawk.yml` file to point to your own StackHawk application ID.

To test the API with HawkScan, run:

```shell
$ hawk scan [--debug] [--log-http]
```
