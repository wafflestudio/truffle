FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . /app
RUN ./gradlew :app:bootJar
EXPOSE 8080
ENTRYPOINT java $JAVA_OPTS -jar app/build/libs/app.jar
