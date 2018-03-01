Bungeecord Minigame
===================

The Game Mode Bungeecord Minigame is a advanced game mode to give BlockBall the rights to manage it's on game server.

This guide will not explain how `BungeeCord <https://www.spigotmc.org/wiki/bungeecord/>`__  works so do not continue if you are not familiar with it.

Make sure you created a `Base Game <../gamemodes/basicgame.html>`__ first before you continue.

Please take a look at associated words which will be used in this guide:

* Gameserver: Server where the BlockBall arena is located and BlockBall is managing it.
* Hubserver: Any other server where players can join the Gameserver by clicking a Blockball sign.

Configuring on your GameServer
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1. Join your game server with the plugins WorlEdit and BlockBall installed.
2. Create a Base Game on this server.
3. Go to the **Settings Page** of your arena and select the GameType Bungee.
4. Open the **Game Settings Page**

.. image:: ../_static/images/arena12.jpg

3. Set the lobby spawnpoint and the leave spawnpoint.
4. Customize the other available options to your own needs.
5. After you have pressed *Save and Reload* the first time the game is automatically managing the server and starting a game
6. You also need to configure a *Restart Script* to automatically restart the server after a game ends
7. Now go to your BlockBall folder on your Gameserver and configure the newly created **bungeecord.yml** file

**config.yml**
::
    bungeecord:
      motd-restarting: '&cRestarting...'
      motd-waiting-players: '&aWaiting for players...'
      motd-ingame: '&9Ingame'
      sign-restarting: '&cRestarting'
      sign-waiting-players: '&aJoin'
      sign-ingame: '&9Running'
      sign-lines:
        '1': '&lBlockBall'
        '2': <server>
        '3': <state>
        '4': <sumplayers>/<summaxplayers>


8. After finishing the configuration copy the **bungeecord.yml** file and open your Hubserver

Configuring on your Hubserver
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1. Setup your Hubserver server with the plugins WorlEdit and BlockBall installed.
2. Paste the copied **bungeecord.yml** into your BlockBall folder on your Hubserver
3. Make sure the content of the **bungeecord.yml** on your Hubserver is exactly the same as the **bungeecord.yml** on your Gameserver
4. Enable the server linking config option in your **config.yml** on your Hubserver

**config.yml**
::
    ############################

    # Game settings

    # allow-server-linking: Enable this to allow linking via BungeeCord between other servers with BlockBall installed.

    ############################

    game:
      allow-server-linking: true

5. Join your Hubserver and type **/blockballbungeecord <server>** (<server> name of your Gameserver)
6. Rightclick on a sign and if everything is working correctly the sign will display something
7. You probably need to restart your Gameserver or change the server name if the sign displays *No connection*
8. Finally, you can join the game by clicking on the sign.


Changing Motd and sign content
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Everything related to the signs and motd can be edited in the **bungeecord.yml** file.

**Important!** Make sure the content of bungeecord.yml is exactly the same on all of your servers in your network.

