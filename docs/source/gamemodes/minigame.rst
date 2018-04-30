MiniGame
========

The Game Mode Minigame is a advanced game mode to use BlockBall as an ordinary Minigame. It contains a lobby and a max amount of time
a game can last. Also, players can only join before the match starts.

Make sure you created a `Base Game <../gamemodes/basicgame.html>`__ first before you continue.


Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena
2. Change the GameType to MiniGame
3. Open the **Game Settings Page**

.. image:: ../_static/images/arena11.jpg

4. Set the lobby spawnpoint and the leave spawnpoint.
5. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this `page <../general/database.html#editing-the-arena-files>`__.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**
::
    meta:
      max-score: 10
      even-teams: false
    minigame-meta:
      match-duration: 300
      lobby-duration: 20
      lobby-spawnpoint:
        x: 175.92141252808105
        y: 65.0
        z: 139.1003464937666
        yaw: 322.1365661621094
        pitch: 11.550313949584961
        world: world
      remaining-players-message: '&aWaiting for &c<remaining> &a more player(s)...'

3. Customize the available options to your own needs.

Properties
~~~~~~~~~~

* Max Score: Max amount of goals the players can score before the arena resets itself back to zero.
* Leave Spawnpoint: Spawnpoint for players who leave the game. This property **has to be set** for MiniGames.
* Gamemode: Minecraft gamemode (Survival, Adventure, Creative) the player is spawning inside of the arena
* Even teams enabled: Should the team choice of the player be ignored if too many players are on one team?
* Max Duration: Max amount of time in seconds how a match can last.
* Lobby Spawnpoint: Position where players spawn when they join the game. This property **has to be set** for MiniGame.
* Lobby Duration: Amount of time in seconds until a match starts after the lobby has reached the min amount of players.
* Remaining Players Message: Message being sent to players when they are waiting for more players in the lobby.

Winning condition
~~~~~~~~~~~~~~~~~

Also, you should take notice of the fact that you can now choose the winning condition for games.

* Setting the Max Score low (5) and the Max Duration high (500) causes the first team to reach 5 goals in 500 seconds to win.
* Setting the Max Score high (100) and the Max Duration low (200) causes the team with the most goals in 200 seconds to win the match.






























