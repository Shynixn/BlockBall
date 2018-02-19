HubGame
=======

The Game Mode HubGame is a simply game mode to provide players a small little area on your hub, lobby or overworld where
they can play soccer anytime without any restrictions or lobbies.

Make sure you created a base game first before you continue.

As the default Game Mode is already a HubGame you do not have to configure anything at all. The Base Game is basically a HubGame.

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the 'Settings' page of your arena chat UI. If not, please take a look at this page.

1. Go to the **Settings Page** of your arena
2. Open the **Game Settings Page**

.. image:: ../_static/images/arena10.jpg

3. Customize all available
4. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this page.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**
::
    meta:
      max-score: 10
      even-teams: false
    hubgame-meta:
      join-selection:
      - Click on the team to join the match.
      - '&c[Team Red]'
      - '&9[Team Blue]'
      instant-forcefield-join: false
      reset-arena-on-empty: false

3. Customize the available options to your own needs.

Properties
~~~~~~~~~~

* Max Score: Max amount of goals the players can score before the arena resets itself back to zero.
* Leave Spawnpoint: Spawnpoint for players who leave the game. If you leave it empty, players clicking on an leave sign will not be teleported and simply kicked out of the forcefield.
* Even teams enabled: Should the team choice of the player be ignored if too many players are on one team?
* Join Message: Message being played when a player runs into the forcefield. Clicking line 2 will cause the player to join the red team, clicking line 3 causes joining the blue team.
* Reset on empty: Should the arena be reset to 0 scores when nobody is playing in it?
* Instant forcefield join: Should players instantly join the game, automatically put into a team and without getting teleported to a spawnpoint?























