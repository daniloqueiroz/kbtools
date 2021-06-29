FROM openjdk:11.0.11-jre-slim
COPY build/distributions/kbtools-1.0-SNAPSHOT.tar .
RUN tar -xf kbtools-1.0-SNAPSHOT.tar 
RUN mv kbtools-1.0-SNAPSHOT /app
ENTRYPOINT ["/app/bin/kbtools"]
