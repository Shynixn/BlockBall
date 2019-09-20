GameProperties
==============

BlockBall offers additional game properties which are just default values which can be changed but are not necessary to take
a look into.

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena
2. Open the **Misc Page**
3. Open the **Game Properties Page**

.. image:: ../_static/images/customizing2.JPG

4. Now continue by enabling or disabling the values.
5. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this `page <../general/database.html#editing-the-arena-files>`__.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**

.. code-block:: yaml

   customizing-meta:
      damage-enabled: false
      score-back-teleport: false
      score-back-teleport-delay: 2
      ball-forcefield: true
      keep-inventory: false

3. Now continue by enabling or disabling the values by changing the **enabled** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Damage enabled: Should players be able to hit each other inside of the arena?
* Score teleport back: Should players be teleported back to their spawnpoint (teamspawnpoint or ballspawnpoint) after someone scores a point?
* Score teleport delay: The amount of seconds after players get teleported back to their spawnpoint if 'Score teleport back' is enabled.
* Ball forcefield: Should the ball stay automatically inside of the arena via an invisible forcefield?
* Keep inventory: Should the players keep their current inventory when they join a game?








