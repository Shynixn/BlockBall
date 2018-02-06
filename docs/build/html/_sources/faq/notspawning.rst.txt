The petblock does not spawn?
============================

It is important to understand how PetBlocks works in order to fix this problem. A PetBlock consist out of
2 entities, an **armorstand** and a **rabbit/zombie** (depending on the movement type).

Simply check if armorstands, rabbits and zombies are allowed to spawn in your world or region and change these
settings if necessary. It is often easier to simple restrict mob spawning by using a game rule. **/gamerule doMobSpawning false**

Also, PetBlocks automatically manages WorldGuard region restrictions so you do not have to manage this on your own.

