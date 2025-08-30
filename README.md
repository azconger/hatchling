# Hatchling API

A knockabout API for testing things that use APIs, like StackHawk. Features MongoDB Atlas-style digest authentication for security testing.

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
* The API should be available at http://localhost:8080/.
* The OAS is available as JSON at http://localhost:8080/openapi.
* The OAS is available as YAML at http://localhost:8080/openapi.yaml.
* The Swagger UI is available at http://localhost:8080/swagger.

## Digest Authentication

The API includes MongoDB Atlas-style digest authentication. The protected endpoint `/api/v1/user/profile` requires digest auth, while other GET endpoints are publicly accessible.

### Pre-configured Test Credentials

- Username: `testuser`
- Password: `abcdef12-3456-7890-abcd-ef1234567890`

### Testing Digest Auth

Test with curl:
```shell
$ curl --digest -u "testuser:abcdef12-3456-7890-abcd-ef1234567890" "http://localhost:8080/api/v1/user/profile"
```

Test with the provided Python script:
```shell
$ python3 digest-auth.py --url http://localhost:8080/api/v1/user/profile --username testuser --password abcdef12-3456-7890-abcd-ef1234567890
```

Or using environment variables:
```shell
$ HAWK_USERNAME="testuser" HAWK_PASSWORD="abcdef12-3456-7890-abcd-ef1234567890" HAWK_URL="http://localhost:8080/api/v1/user/profile" python3 digest-auth.py
```

### External Command Authentication

The `digest-auth.py` and `digest-auth.sh` scripts output StackHawk-compatible JSON for External Command Authentication:

```json
{"headers": [{"Authorization": "Digest username=\"testuser\", realm=\"MongoDB Atlas API\", ..."}], "cookies": []}
```

## Testing with StackHawk

> NOTE: You may need to update the `stackhawk.yml` file to point to your own StackHawk application ID.

To test the API with HawkScan, run:

```shell
$ hawk scan [--help] | [--verbose] [--debug] [--log-http]
```
