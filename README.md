# Clevo-Service-ASR
Java based servlet service for speech transcription

## Structure

```
index.html - GraphiQL client
/graphql - Service endpoint
/test - Service configuration
```

## Running
Compile and package (build/libs/Clevo-Service-ASR.war)

`./gradlew build`

Run locally on jetty (http://localhost:8080/Clevo-Service-ASR)

`./gradlew appRun`

Run locally using docker-compose
1. Copy docker-compose.yml file from Clevo-Docker-Compose-Aliyun project to this directory
2. run `cp ./build/libs/Clevo-Service-ASR.war ./app/java && docker-compose up`
