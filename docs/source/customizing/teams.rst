Teams
=====

There are 2 available Teams in BlockBall who play against each other. They are named blue and red per default and will also
be associated like that in the Chat UI and in this guide.

Of course it is possible to recolor them to something like yellow or pink, but all placeholders and the chat navigation will always stay like they are.

.. image:: ../_static/images/team0.png

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena

.. image:: ../_static/images/team1.jpg

2. Choose **Team Red** or **Team Blue** to configure.

.. image:: ../_static/images/team2.jpg

3. Customize each team to your needs.

.. image:: ../_static/images/team5.jpg

4. The Teams also provide so called `Textbook Settings <textbook.html>`_ which are Team specific messages.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this page.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml - red-team**
::
   team-red:
      displayname: Team Red
      prefix: '&c'
      min-amount: 0
      max-amount: 10
      walking-speed: 0.2
      goal:
        corner-1:
          x: 134.0
          y: 80.0
          z: 251.0
          yaw: 0.0
          pitch: 0.0
          world: world
        corner-2:
          x: 133.0
          y: 80.0
          z: 250.0
          yaw: 0.0
          pitch: 0.0
          world: world
      armor:
        '1':
          type: LEATHER_BOOTS
          meta:
            ==: ItemMeta
            meta-type: LEATHER_ARMOR
            color: &id001
              ==: Color
              RED: 255
              BLUE: 0
              GREEN: 0
        '2':
          type: LEATHER_LEGGINGS
          meta:
            ==: ItemMeta
            meta-type: LEATHER_ARMOR
            color: *id001
        '3':
          type: LEATHER_CHESTPLATE
          meta:
            ==: ItemMeta
            meta-type: LEATHER_ARMOR
            color: *id001
      join-message: You have joined the game.
      leave-message: You have left the game.
      lines:
      - '&lBlockBall'
      - <game>
      - <teamcolor><team>
      - <players>/<maxplayers>
      score-message-title: '<redcolor><redscore> : <bluecolor><bluescore>'
      score-message-subtitle: <redcolor><player> scored for <red>
      win-message-title: <redcolor><red>
      win-message-subtitle: <red>&a has won the match

**arena_x.yml - red-team**
::
   team-blue:
      displayname: Team Blue
      prefix: '&9'
      min-amount: 0
      max-amount: 10
      walking-speed: 0.2
      goal:
        corner-1:
          x: 134.0
          y: 80.0
          z: 251.0
          yaw: 0.0
          pitch: 0.0
          world: world
        corner-2:
          x: 133.0
          y: 80.0
          z: 250.0
          yaw: 0.0
          pitch: 0.0
          world: world
      armor:
        '1':
          type: LEATHER_BOOTS
          meta:
            ==: ItemMeta
            meta-type: LEATHER_ARMOR
            color: &id002
              ==: Color
              RED: 0
              BLUE: 255
              GREEN: 0
        '2':
          type: LEATHER_LEGGINGS
          meta:
            ==: ItemMeta
            meta-type: LEATHER_ARMOR
            color: *id002
        '3':
          type: LEATHER_CHESTPLATE
          meta:
            ==: ItemMeta
            meta-type: LEATHER_ARMOR
            color: *id002
      join-message: You have joined the game.
      leave-message: You have left the game.
      lines:
      - '&lBlockBall'
      - <game>
      - <teamcolor><team>
      - <players>/<maxplayers>
      score-message-title: '<bluecolor><bluescore> : <redcolor><redscore>'
      score-message-subtitle: <bluecolor><player> scored for <blue>
      win-message-title: <bluecolor><blue>
      win-message-subtitle: <blue>&a has won the match

3. Customize the available options to your own needs.

It is recommend to use the ingame one-click armor creation instead of trying to understand the Itemstack format in the arena.yml.

Properties
~~~~~~~~~~

* Name: Name of the team (Placeholder: <blueteam>/<redteam>)
* Color: Color of the team (Placeholder: <bluecolor>/<redcolor>)
* Min amount: Min amount of players required to join this team in order to start a match.
* Max amount: Max amount of players who can be on this team at the same time.
* Armor: Armor of the players wearing during a match.
* Walking Speed: A highly requested option to allow playing on large fields by changing the players default speed value.
* Spawnpoint: Optional spawnpoint for the team if you do not want the players to join at the ball spawnpoint.
* Textbook: All team specific messages which can be found `here <textbook.html>`_ .










