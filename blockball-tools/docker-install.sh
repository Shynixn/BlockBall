wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastStableBuild/artifact/target/BuildTools.jar"
java -Xmx1024M -jar BuildTools.jar --rev "$1"
echo "eula=true" > eula.txt
echo '[
  {
    "uuid": "dd6f3d9b-86e6-497f-b22a-2fc99e046e65",
    "name": "Shynixn",
    "level": 4,
    "bypassesPlayerLimit": false
  }
]' > ops.json
mkdir plugins
mkdir plugins/BlockBall
mkdir plugins/BlockBall/arena
mv arena/* plugins/BlockBall/arena