FROM openjdk:8

ENV SPIGOT_VERSION 1.14.4
ENV WORLD_VERSION 1.14

WORKDIR minecraft
COPY ["blockball-tools/docker-install.sh", "blockball-tools/docker-start.sh", "blockball-tools/world-${WORLD_VERSION}", "blockball-tools/arena-files", "blockball-bukkit-plugin/build/libs", "./"]
RUN ["sh","-c","sed -i 's/\r//g' docker-install.sh && sed -i 's/\r//g' docker-start.sh && ./docker-install.sh ${SPIGOT_VERSION}"]
CMD ["sh","-c","./docker-start.sh ${SPIGOT_VERSION} && tail -f /dev/null"]

EXPOSE 25565