## http-pie

Portable Java HTTP server for file sharing with custom network throttling and support for handling multiple requests simultaneously.
Sometimes you just want a small server to share files over http, this works similar to SimpleHttpServer from python but supports
multiple requests.

## How to build it?

    ./gradlew jar

## How to run it?

    $JAVA_HOME11/bin/java -jar http-pie-1.0-SNAPSHOT.jar
    $JAVA_HOME11/bin/java -jar http-pie-1.0-SNAPSHOT.jar [port] [/path/to/share]
    $JAVA_HOME11/bin/java -jar http-pie-1.0-SNAPSHOT.jar [port] [/path/to/share] [apache]

## Recommended way to use it?

Run the software as a daemon pointing to a NAS and put it behind of a load balancer to have a nice
distributed download software.

![Architecture](https://raw.githubusercontent.com/donhk/http-pie/main/docs/architecture.png "Architecture")



