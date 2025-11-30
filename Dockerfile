FROM eclipse-temurin:17-jre

WORKDIR /app

COPY TCPServer.jar /app/TCPServer.jar

EXPOSE 5050

CMD ["java", "-jar", "TCPServer.jar"]
