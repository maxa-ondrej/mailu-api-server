# App Building phase --------
FROM openjdk:8 AS build

RUN mkdir /appbuild
COPY . /appbuild

WORKDIR /appbuild

#RUN ./gradlew clean build
# End App Building phase --------

# Container setup --------
FROM mailu/admin:1.9

RUN apk update
RUN apk add openjdk8

# Creating user
ENV APPLICATION_USER 1033
RUN adduser -D -g '' $APPLICATION_USER

# Giving permissions
RUN mkdir /api
RUN mkdir /api/resources
RUN chown -R $APPLICATION_USER /api
RUN chmod -R 755 /api

# Setting user to use when running the image
USER $APPLICATION_USER

# Copying needed files
COPY --from=build /appbuild/build/libs/api*all.jar /api/application.jar
#COPY --from=build /appbuild/resources/ /api/resources/
WORKDIR /api

# Entrypoint definition
CMD ["sh", "-c", "java -server -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:InitialRAMFraction=2 -XX:MinRAMFraction=2 -XX:MaxRAMFraction=2 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -jar application.jar"]
# End Container setup --------