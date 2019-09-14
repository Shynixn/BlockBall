mv blockball-bukkit-plugin* plugins
java -Xmx1024M -Xmx1024M -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spigot-"$1".jar -o false