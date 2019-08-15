Bungeecord Minigame
===================

The Game Mode Bungeecord Minigame is a advanced game mode to give BlockBall the rights to manage it's on game server.

.. warning:: This guide will not explain how `BungeeCord <https://www.spigotmc.org/wiki/bungeecord/>`__  works so do not continue if you are not familiar with it.

Make sure you created a `Base Game <../gamemodes/basicgame.html>`__ first before you continue.

Please take a look at associated words which will be used in this guide:

* Gameserver: Server where the BlockBall arena is located and BlockBall is managing it.
* Hubserver: Any other server where players can join the Gameserver.

Configuring on your GameServer
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Let's start by creating a server where BlockBall manages its state.

1. Join your game server with the plugin BlockBall installed.
2. Create a Base Game on this server.
3. Go to the **Settings Page** of your arena and select the GameType Bungee.
4. Open the **Game Settings Page**

.. image:: ../_static/images/arena12.JPG

5. Set the lobby spawnpoint and the leave spawnpoint.
6. Customize the other available options to your own needs.

.. note:: If you have got just **one Hubserver** you can simply enter the Hubserver name into the **Fallback Server** setting to let players get automatically connected back to the Hubserver if a match ends.
.. note:: If you have got **multiple Hubserver** you should edit the **Leave Server Kick Message** to create a custom kick message. Install a BungeeCord plugin which redirects players across your network when a player gets kicked from a server with a certain message.

5. After you have pressed *Save and Reload* the first time the game is automatically managing the server and starting a game.
6. You also need to configure a *Restart Script* to automatically restart the server after a game ends.

.. note:: You can optionally change the server motd by editing the generated **bungeecord.yml** file in the BlockBall folder.

Configuring on your Hubserver
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

BlockBall contains a system to offer signs which connects BlockBall Gameserver to Hubservers. This means you can
place signs on your HubServer which connect players to Gameservers.

.. note:: The following section is entirely **optional**. It is probably even better to install a minigame server management plugin to allow sign joining.

1. Setup your Hubserver server with the plugin BlockBall installed.
2. Copy the **bungeecord.yml** file from your Gameserver folder into the Hubserver folder.
3. Make sure the content of the **bungeecord.yml** on your Hubserver is exactly the same as the **bungeecord.yml** on your Gameserver.
4. Enable the server linking config option in your **config.yml** on your Hubserver

**config.yml**

.. code-block:: yaml

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