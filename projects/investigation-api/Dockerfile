FROM clojure AS clj_container
RUN mkdir /app
COPY . /app
WORKDIR /app/projects/investigation-api
RUN clojure -T:build uber

RUN mv /app/projects/investigation-api/target/investigation-api.jar /
RUN rm -rf /app

FROM amazoncorretto
COPY --from=clj_container /investigation-api.jar /

EXPOSE 3003
CMD ["java", "-jar", "/investigation-api.jar"]
