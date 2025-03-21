## Game webflux websocket server demo

# Attention! Highload java game server framework being in active development process now is available for usage and contribution. If you like It - support this tool. https://github.com/tfkfan/orbital

Java highload game server demo with netty/webflux reactive features
and matchmaking support

### Build and run

```
./mvnw clean verify spring-boot:run
```

### Properties

Server uses spring-boot-web to start http server, 
therefore - all spring boot properties are available for usage.
Custom properties are declared as ApplicationProperties.java.

application.yml:
```
application:
  room:
    max-players: 2
    loop-rate: 100
    end-delay: 30000
server:
    port: 8080
```
### Html example
Simple html/js example already included as a static web resources,
just look at localhost:8080. Click login button and wait for game room and match start

