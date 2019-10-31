# pact-provider-state
A simple application used to setup and teardown 
[Pact provider states](https://docs.pact.io/getting_started/provider_states) 
supplied to the application via config. 

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
> $ docker run --rm rdens1/pact-provider-state
```

Note that each commit to master is deployed to [https://hub.docker.com/r/rdens1/pact-provider-state](https://hub.docker.com/r/rdens1/pact-provider-state).
Each deployment is tagged with `latest` and the short SHA-1 for the commit (e.g. `git rev-parse --short HEAD`)

## Configuration
The configurations that are made available for others who might want to use
this application can be found, with defaults at 
[app/src/main/resources/reference.conf](app/src/main/resources/reference.conf). 
Additional configurations should be specified by creating an `application.conf` 
file in the same directory as `reference.conf`. Note that `application.conf` is 
ignored from git (see [.gitignore](.gitignore)).

To configure the provider states which can be setup, simply configure the `providerStates`
variable in your configuration file. Note that the `state` field must correspond to
the same value as the `name` field sent by a pact verifier, such as 
[pact-jvm-provider-maven](https://github.com/DiUS/pact-jvm/tree/master/provider/pact-jvm-provider-maven)
```
providerStates = [
   {
        "state": "Ryan is in the DB",
        "setupQuery": "INSERT into users(name,age) VALUES('Ryan', 23)",
        "teardownQuery": "DELETE from users WHERE name = 'Ryan';"
   },
   {
        "state": "Jane is in the DB",
        "setupQuery": "INSERT into users(name,age) VALUES('Jane', 22)",
        "teardownQuery": "DELETE from users WHERE name = 'Jane';"
   }
]
```

