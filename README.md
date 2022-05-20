# casual-standalone
casual java standalone client code for invocations towards casual

## Run application
```bash
CASUAL_HOST=10.101.58.228 CASUAL_PORT=7771 ./gradlew run
```

## Execute

### Service request

Example requires that this casual service is available from CASUAL_HOST:CASUAL_PORT
```bash
curl -v -d @curl-data -H 'content-type:application/octet-stream' http://localhost:7575/casual/casual%2fexample%2fecho
```

### Enqueue request

curl -v -d @curl-data -H 'content-type:application/octet-stream' http://localhost:7575/casual/enqueue/my-queue


### Dequeue request

curl http://localhost:7575/casual/dequeue/my-queue

