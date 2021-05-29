# This dockerfile uses multistage-builds (this is the build stage)
FROM openjdk:8 AS builder
WORKDIR /tmp
RUN apt-get update
RUN apt-get install maven -y
RUN apt-get install dos2unix -y
# Execute the commands below on your system to setup the build environment for your IDE.
RUN wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
RUN java -jar BuildTools.jar --rev 1.16.4
RUN mvn install:install-file -Dfile=spigot-1.16.4.jar -DgroupId=org.spigotmc -DartifactId=spigot116R3 -Dversion=1.16.4-R3.0 -Dpackaging=jar
RUN wget "https://jitpack.io/com/github/MilkBowl/VaultAPI/1.7/VaultAPI-1.7.jar"
RUN mvn install:install-file -Dfile=VaultAPI-1.7.jar -DgroupId=net.milkbowlvault -DartifactId=VaultAPI -Dversion=1.7 -Dpackaging=jar
# Build the BlockBall.jar
COPY . /tmp
RUN dos2unix gradlew
RUN ./gradlew build shadowJar --no-daemon

FROM openjdk:8
# Location where the server is running.
WORKDIR /app
RUN echo "eula=true" > eula.txt
RUN mkdir plugins && mkdir plugins/BlockBall
COPY --from=builder /tmp/spigot-1.16.4.jar .
COPY --from=builder /tmp/blockball-bukkit-plugin/build/libs/* /app/plugins/
COPY ./blockball-tools/arena-files/ /app/plugins/BlockBall
COPY ./blockball-tools/world-1.14/ /app/
COPY ./blockball-tools/permissions.yml /app/
COPY ./blockball-tools/ops.json /app/
# Port of the Minecraft Server.
EXPOSE 25565
# Port for Remote Debugging
EXPOSE 5005
CMD ["sh","-c","java -DIReallyKnowWhatIAmDoingISwear -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spigot-1.16.4.jar"]
