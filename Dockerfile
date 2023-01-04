FROM gradle:7.6.0-jdk17 as builder

WORKDIR /build
COPY ./ /build

RUN gradle shadowjar

FROM amazoncorretto:17-alpine

WORKDIR /home/PrivateRooms
COPY --from=builder /build/build/libs/*.jar /PrivateRooms.jar

ENTRYPOINT ["java","-jar","/PrivateRooms.jar"]
