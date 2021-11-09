FROM gradle AS builder
RUN gradle --version && java -version
COPY gradle.properties build.gradle.kts settings.gradle.kts /build/
WORKDIR /build
RUN gradle clean build --no-daemon --info --stacktrace || true
COPY . /build/
RUN gradle build :distTar -x test --info --stacktrace

FROM openjdk:slim
RUN mkdir -p /server
COPY --from=builder /build/build/distributions/server-0.0.1.tar /server
WORKDIR /server
RUN tar -xvf server-0.0.1.tar
CMD ["./server-0.0.1/bin/server", "-port=80", "-P:database.driver=org.postgresql.Driver", "-P:database.url=jdbc:postgresql://db/memster", "-P:database.username=memster", "-P:database.password=memster"]
