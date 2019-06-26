# Chat application for MocBackendChallenge


Default port: 8090

## Steps to start application
1. Start Postgres using Docker-compose.yml
2. Create chat DB in Postgres
3. Execute create.sql script
4. Start moc_chat application

Next env variables can be overrided:
* DB_URL
* DB_USERNAME
* DB_PASSWORD


## Docker commands

```
docker build -t moc_chat:1.0.0 .

docker run -d -p 8090:8090 --name moc_chat moc_chat:1.0.0

docker-compose up
docker-compose -f docker-compose_db.yml up
```

## STOMP Client

```
    var ws = new WebSocket('ws://localhost:8090/ws?x-user-id=<userId>');
    ws.onmessage = function(data) {
        console.log(data.data);
    };
```

## TODOs list
* Change WebSocket port to 1030
* Create new entity to save user credentials. User service should retrieve Entity without password.
* Review and check exception handling
* Add payload logging
* Can't connect to ws with SSL

## Notes
For some reasons in docker-compose password matchers does not work.

It's strange because it works well on when I run jar file on local machine
