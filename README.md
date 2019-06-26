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

docker run -d -p 8090:8090 -p 1030:8090 --name moc_chat moc_chat:1.0.0

docker-compose up
docker-compose -f docker-compose_db.yml up
```

## WS Client
To be able to connect from Chrom to our application without checking SSL certificates
we need to run Chrom browser with next flag
```
"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --ignore-certificate-errors
```

```
    var ws = new WebSocket('ws://localhost:8090/ws?x-user-id=<userId>');
    ws.onmessage = function(data) {
        console.log(data.data);
    };
```

## TODOs list
* Create new entity to save user credentials. User service should retrieve Entity without password.
* Review and check exception handling
* Add payload logging