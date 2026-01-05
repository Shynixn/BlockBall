# This docker file uses multi-stage builds to build multiple minecraft versions in parallel.
# Minecraft JDK 8
FROM adoptopenjdk/openjdk8 AS buildtools-jdk8
WORKDIR /tmp
RUN apt-get update && apt-get install maven wget git -y
RUN wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
RUN java -jar BuildTools.jar --rev 1.8.8 && cp /tmp/spigot-1.8.8.jar /root/.m2/repository/org/spigotmc/spigot-1.8.8.jar
#RUN java -jar BuildTools.jar --rev 1.9.4
#RUN java -jar BuildTools.jar --rev 1.13.2
#RUN java -jar BuildTools.jar --rev 1.16.4

# Minecraft JDK 17
FROM amazoncorretto:17 AS buildtools-jdk17
WORKDIR /tmp
RUN yum update -y
RUN yum install maven -y
RUN yum install wget -y
RUN yum install git -y
RUN wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
#RUN java -jar BuildTools.jar --rev 1.17.1 --remapped
RUN java -jar BuildTools.jar --rev 1.18 --remapped && cp /tmp/spigot-1.18.jar /root/.m2/repository/org/spigotmc/spigot-1.18.jar
#RUN java -jar BuildTools.jar --rev 1.18.2 --remapped
#RUN java -jar BuildTools.jar --rev 1.19 --remapped
#RUN java -jar BuildTools.jar --rev 1.19.3 --remapped
#RUN java -jar BuildTools.jar --rev 1.19.4 --remapped
#RUN java -jar BuildTools.jar --rev 1.20.1 --remapped
#RUN java -jar BuildTools.jar --rev 1.20.2 --remapped
#RUN java -jar BuildTools.jar --rev 1.20.4 --remapped

# Minecraft JDK 21
FROM amazoncorretto:21 AS buildtools-jdk21
WORKDIR /tmp
RUN yum update -y
RUN yum install maven -y
RUN yum install wget -y
RUN yum install git -y
RUN wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
#RUN java -jar BuildTools.jar --rev 1.20.6 --remapped
#RUN java -jar BuildTools.jar --rev 1.21 --remapped
#RUN java -jar BuildTools.jar --rev 1.21.1 --remapped
#RUN java -jar BuildTools.jar --rev 1.21.3 --remapped
#RUN java -jar BuildTools.jar --rev 1.21.4 --remapped
#RUN java -jar BuildTools.jar --rev 1.21.5 --remapped
#RUN java -jar BuildTools.jar --rev 1.21.8 --remapped
#RUN java -jar BuildTools.jar --rev 1.21.9 --remapped
#RUN java -jar BuildTools.jar --rev 1.21.10 --remapped
RUN java -jar BuildTools.jar --rev 1.21.11 --remapped && cp /tmp/spigot-1.21.11.jar /root/.m2/repository/org/spigotmc/spigot-1.21.11.jar
RUN mkdir -p /root/.m2/repository/io/papermc && wget -O /root/.m2/repository/io/papermc/paper-1.21.11.jar "https://fill-data.papermc.io/v1/objects/5be84d9fc43181a72d5fdee7e3167824d9667bfc97b1bf9721713f9a971481ca/paper-1.21.11-88.jar"

# Build image for all versions.
FROM eclipse-temurin:21-jdk
LABEL stage=buildtools
RUN apt-get update -y && apt-get install -y git wget
COPY --from=buildtools-jdk8 /root/.m2/repository/org/spigotmc /root/.m2/repository/org/spigotmc/
COPY --from=buildtools-jdk17 /root/.m2/repository/org/spigotmc /root/.m2/repository/org/spigotmc/
COPY --from=buildtools-jdk21 /root/.m2/repository/org/spigotmc /root/.m2/repository/org/spigotmc/
COPY --from=buildtools-jdk21 /root/.m2/repository/io/papermc /root/.m2/repository/io/papermc/

# Setup test server
WORKDIR /app
RUN mkdir /app/plugins
RUN cp /root/.m2/repository/org/spigotmc/spigot-1.21.11.jar /app/spigot.jar 
RUN cp /root/.m2/repository/io/papermc/paper-1.21.11.jar /app/paper.jar 
RUN echo "eula=true" > /app/eula.txt
RUN mkdir -p /app/world
RUN echo "server-port=25565" >> /app/server.properties
RUN echo "online-mode=false" >> /app/server.properties
RUN echo "motd=Devcontainer Spigot Server" >> /app/server.properties   
RUN echo "java -Djava.awt.headless=true -jar spigot.jar --nogui" > /app/start-spigot.sh && chmod +x /app/start-spigot.sh
RUN echo "java -Djava.awt.headless=true -jar paper.jar --nogui" > /app/start-paper.sh && chmod +x /app/start-paper.sh

# Set working directory
WORKDIR /workspace

# Set environment variable
ENV LOCAL=true