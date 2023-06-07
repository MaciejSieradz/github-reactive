# Github task - reactive 

## About the project

This project is communicating with GitHub REST API and allow users to
search repositories by provided username.

## Testing

To test this application simply download the project, go to project file and run
`./mvnw test`. You can also run tests from your favourite IDE.

## Build & run

Since this is a maven project, you can run it in your IDE or you can use
`./mvnw spring-boot:run`. If you want to deploy it to `.jar` file, build it with
`./mvnw package` and then run with `java -jar target/Github-reactive-0.0.1-SNAPSHOT.jar` command.

Once the application is running, you can retrieve information from the API using
`curl` like this:

```bash
$ curl http://localhost:8080/repositories/{username}
```

If you want to get in prettier format, you can always use `json` command like this
(You will probably have to install it)

```bash
$ curl http://localhost:8080/repositories/MaciejSieradz | json
```

## Important

GitHub REST API has limited number of requests. If you want to get more, you will have to
add your authentication token to rest request. See more information
[here](https://docs.github.com/en/rest/overview/resources-in-the-rest-api?apiVersion=2022-11-28#rate-limiting).