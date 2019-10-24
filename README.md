# pact-provider-state
A simple application used to setup and teardown pact provider states


## Development

To run the build and tests, simply run the following command:

```shell script
> $ ./gradlew check
```

To run the application using the Gradle Application plugin, run the following command:
```shell script
> $ ./gradlew run
```

To run the application with Docker, run the following commands

```shell script
> $ ./gradlew jibDockerBuild # builds the image
> $ docker run --rm ryandens/pact-provider-state
```