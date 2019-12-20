FROM openjdk:8

WORKDIR /minecraft

COPY ["blockball-tools", "blockball-bukkit-plugin/build/libs", "./"]

RUN wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastStableBuild/artifact/target/BuildTools.jar" && \
    java -Xmx1024M -jar BuildTools.jar --rev "1.14.4" && \
    echo "eula=true" > eula.txt

CMD rm -r plugins/BlockBall -f && \
    rm -r world -f && \
    rm -r world_nether -f && \
    rm -r world_the_end -f && \
    cp -r world-1.14/world . && \
    mkdir -p plugins/BlockBall/arena && \
    cp -r arena-files/arena/* plugins/BlockBall/arena && \
    java -Xmx1024M -Xmx1024M -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spigot-1.14.4.jar