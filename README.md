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

## Configuration
The configurations that are made available for others who might want to use
this application can be found, with defaults at 
[reference.conf](app/src/main/resources/reference.conf). Additional configurations
should be specified by creating an `application.conf` file in the same directory.
Note that `application.conf` is ignored from git (see [.gitignore](.gitignore)).
