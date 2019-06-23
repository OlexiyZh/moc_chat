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
```

## STOMP Client

```
    var socket = new SockJS('http://localhost:8090/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect(onConnected, onError);
    
    stompClient.subscribe('/topic', onMessageReceived);
    
    function onMessageReceived(payload) {
        var message = JSON.parse(payload.body);
        console.log(message);
    }
```

## TODOs list
* Change WebSocket port to 1030
* Implement constraints validation for password and userName
* Create new entity to save user credentials. User service should retrieve Entity without password.
* Send message to specific group of users by roomId
* Review and check exception handling
* Add payload logging
* Add certificate to support https

## Notes
For some reasons in docker-compose password matchers does not work.

It strange because it works well on when I run jar file on local machine 
