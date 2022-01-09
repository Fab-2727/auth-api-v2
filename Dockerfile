# syntax=docker/dockerfile:1

FROM openjdk:11

RUN mkdir /auth-api
WORKDIR /auth-api

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src

#   =>  SERVER CONFIG
ENV SERVER_PORT="8081"
#   =>  DATABASE CONFIG
ENV JPA_HIBERNATE_DDL="update"
ENV DATABASE_URL="jdbc:mysql://localhost:3306/auth_2_api"
ENV DATABASE_USERNAME="root"
ENV DATABASE_PASSWORD="admin"
ENV DATABASE_DRIVER="com.mysql.cj.jdbc.Driver"
ENV JPA_HIBERNATE_DIALECT="org.hibernate.dialect.MySQL5Dialect"
#   =>  TOKEN CONFIG
ENV TOKEN_SECRET='NcRfUjXn2r5u8x/A?D(G-KaPdSgVkYp3s6v9y$$B&E)H@MbQeThWmZq4t7w!z%C*F'
ENV TOKEN_EXPIRED_TIME_MS="3600"
#   =>  SERVICE CONFIG
ENV SECURITY_ROLE_HIERARCHY="ROLE_ADMIN > ROLE_STAFF ROLE_STAFF > ROLE_USER"
ENV LOG_LEVEL="INFO"

CMD ["./mvnw", "clean", "spring-boot:run"]
