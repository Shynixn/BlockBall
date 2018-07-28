Spectatormode
=============

The spectator mode settings allow to setup spectating for players.

This allows players to start spectator by using the **/bbspectate <game>** command or the suggestion message when the game is full.

The spectator mode is per default enabled for Minigames.

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena
2. Open the **Spectating Page**
3. Open the **Spectatormode Page**

.. image:: ../_static/images/spectatormode_1.jpg

4. Now continue by enabling or disabling the mode by clicking **toggle** with your mouse.
5. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this `page <../general/database.html#editing-the-arena-files>`__.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**
::
   spectator-meta:
      spectatormode-enabled: false
      spectatormode-spawnpoint:
        x: 265.63449057674416
        y: 69.0
        z: 72.48389102002054
        yaw: -3.3080687522888184
        pitch: 13.950082778930664
        world: world
      spectatormode-start-message:
      - Do you want to spectate this match?
      - §a[Start spectating]

3. Now continue by enabling or disabling the spectator mode by changing the **spectatormode-enabled** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Spectatormode-enabled: Enable or disable the spectator mode for nearby players.
* Spectatormode-spawnpoint: Spawnpoint for players who start spectating.
* spectatormode-start-message: Message being sent to players who try to join a full game.










