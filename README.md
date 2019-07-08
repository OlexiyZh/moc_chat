# Chat application for MocBackendChallenge

Default port: 8090

## Steps to start application
1. Execute mvn clean package in root folder with pom.xml
2. Build docker image for moc_chat application
3. Start docker-compose.yml
4. Disable SSL certificate verification in Postman if you use this tool for testing

Next env variables can be overrided:
* DB_URL
* DB_USERNAME
* DB_PASSWORD


## Docker commands

```
docker build -t moc_chat:1.0.0 .
docker build -t alexeyzh/moc_chat:1.0.0 .

docker login --username=alexeyzh
docker push alexeyzh/moc_chat:1.0.0

docker run -d -p 8090:8090 -p 1030:8090 --name moc_chat moc_chat:1.0.0

docker-compose up
docker-compose -f docker-compose_local.yml up
docker-compose -f docker-compose_db.yml up
```

## WS Client
To be able to connect from Chrome to our application without checking SSL certificates
we need to run Chrome browser with next flag
```
"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --ignore-certificate-errors
```

```
var ws = new WebSocket('wss://localhost:8090/ws?x-user-id=<userId>');
ws.onmessage = function(data) {
    console.log(data.data);
};
```

## API Specification

http://localhost:8090/swagger-ui.html

## Chat bots configuration

2 chat bot are available:
* Echobot
* Dialogflow bot

To address you message to Echo bot you need add @echobot before you message

To address you message to Dialogflow bot you need add @bot before you message

Each bot can be disable. You need set `false` value into `DIALOGFLOW_BOT_ENABLED` or `ECHOBOT_ENABLED` to disable corresponding bot.

By default all bots are enabled.

#### Dialogflow bot
Bot can manage context:
```
>> @bot My name is John
>> Got it
>> @bot What's my name?
>> Your name is John
```

Bot supports switching languages:
```
>> @bot switch to ukrainian
>> Гаразд. Далі українською
>> @bot Привіт
>> Вітаю!
>> @bot ok. Перейти на англійську
>> Ok. I will use English now
```

## TODOs list
* Push docker image to docker registry
* Add masking sensitive data for payload logging
* Create new entity to save user credentials. User service should retrieve Entity without password.
* Swagger and Spring Security does not work correctly. Swagger ask fot authorization and when you cancel it works
* Refactor Dialogflow bot
