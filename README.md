# Chat application for MocBackendChallenge


Default port: 8090


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
Change WebSocket port to 1030

Implement constraints validation for password and userName

Create new entity to save user credentials. User service should retrieve Entity without password.

Send message to specific group of users by roomId
