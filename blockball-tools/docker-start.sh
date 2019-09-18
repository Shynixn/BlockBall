rm -r plugins/BlockBall
rm -r world
rm -r world_nether
rm -r world_the_end
mkdir plugins/BlockBall
mkdir plugins/BlockBall/arena
version=$(echo "$1" | tr -d '.')
if [ "$version" -le 113 ]; then
   cp -r world-1.8/world .
else
   cp -r world-1.14/world .
fi;
cp -r arena-files/arena/* plugins/BlockBall/arena
java -Xmx1024M -Xmx1024M -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spigot-"$1".jar -o false