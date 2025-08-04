FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh
ENTRYPOINT ["/app/wait-for-it.sh", "postgres_owners:5432", "--timeout=60", "--", "java", "-jar", "/app/app.jar"]