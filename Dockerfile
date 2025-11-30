FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy your server JAR into the container
COPY TCPServer.jar /app/TCPServer.jar

# Expose the port your server listens on
EXPOSE 5050

# Start the server
CMD ["java", "-jar", "TCPServer.jar"]
