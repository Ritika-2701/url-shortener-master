FROM openjdk:8
COPY build/libs/url-shortener-0.0.1-SNAPSHOT.jar url-shortener.jar
EXPOSE 8081
CMD ["java","-jar","url-shortener.jar"]
