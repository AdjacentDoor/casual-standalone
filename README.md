# casual-standalone
casual java standalone client code for invocations towards casual

## Run application
```bash
CASUAL_HOST=10.101.58.228 CASUAL_PORT=7771 ./gradlew run
```

## Execute request

Example requires that this casual service is available from CASUAL_HOST:CASUAL_PORT
```bash
curl -d @curl-data -H 'content-type:application/casual-x-octet' http://localhost:7575/casual/casual%2fexample%2fecho
```

