FROM gradle:6.7.0-jdk14 as builder

WORKDIR /build
COPY ./ /build

RUN gradle shadowjar

FROM adoptopenjdk/openjdk14:alpine

WORKDIR /home/PrivateRooms
COPY --from=builder /build/build/libs/*.jar /PrivateRoomsReborn.jar

ENTRYPOINT ["java","-jar","/PrivateRoomsReborn.jar"]