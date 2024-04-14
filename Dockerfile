# This docker file uses multi-stage builds.
# 1. Resolve minecraft-dependencies for 1.8 - 1.16 with jdk8
FROM openjdk:8 AS dependencies-jdk8
WORKDIR /tmp
RUN apt-get update
RUN apt-get install maven -y
RUN wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
RUN java -jar BuildTools.jar --rev 1.16.4
RUN wget "https://jitpack.io/com/github/MilkBowl/VaultAPI/1.7/VaultAPI-1.7.jar"
RUN mvn install:install-file -Dfile=VaultAPI-1.7.jar -DgroupId=net.milkbowlvault -DartifactId=VaultAPI -Dversion=1.7 -Dpackaging=jar
CMD ["sh","-c","/bin/bash"]

# 2. Resolve minecraft-dependencies for 1.17 - latest with jdk17
FROM amazoncorretto:17 AS dependencies-jdk17
WORKDIR /tmp
RUN yum update -y
RUN yum install maven -y
RUN yum install wget -y
RUN yum install git -y
RUN wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
RUN java -jar BuildTools.jar --rev 1.20.2 --remapped

# 3. Build plugin for 1.8 - latest with jdk17
FROM amazoncorretto:17 AS plugin-jdk17
WORKDIR /tmp
RUN yum update -y
RUN yum install maven -y
RUN yum install dos2unix -y
COPY --from=dependencies-jdk8 /root/.m2/repository/net/milkbowlvault /root/.m2/repository/net/milkbowlvault/
COPY --from=dependencies-jdk8 /root/.m2/repository/org/spigotmc /root/.m2/repository/org/spigotmc/
COPY --from=dependencies-jdk17 /root/.m2/repository/org/spigotmc /root/.m2/repository/org/spigotmc/
COPY . /tmp
RUN chmod +x gradlew
RUN dos2unix gradlew
RUN ./gradlew build pluginJar --no-daemon

# 4. Launch a minecraft server with jdk17 and plugin
FROM amazoncorretto:17
# Change to the current plugin version present in build.gradle
ENV PLUGIN_VERSION=6.42.1
# Change to the server version you want to test.
ENV SERVER_VERSION=spigot-1.18.jar
# Port of the Minecraft Server.
EXPOSE 25565
# Port for Remote Debugging
EXPOSE 5005
WORKDIR /app
RUN yum update -y
RUN echo "eula=true" > eula.txt && mkdir plugins
COPY ./blockball-tools/arena-files/ /app/plugins/BlockBall
COPY ./blockball-tools/world-1.14/ /app/
COPY ./blockball-tools/ops.json /app/
# For < 1.18 COPY --from=dependencies-jdk17 /root/.m2/repository/org/spigotmc/spigot/$SERVER_VERSION /app/spigot.jar
COPY --from=dependencies-jdk17 /tmp/$SERVER_VERSION /app/spigot.jar
COPY --from=plugin-jdk17 /tmp/blockball-bukkit-plugin/build/libs/blockball-bukkit-plugin-$PLUGIN_VERSION.jar /app/plugins/BlockBall.jar
CMD ["sh","-c","java -DIReallyKnowWhatIAmDoingISwear -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spigot.jar"]
