DoubleJump
==========

It is possible to use a double jump per default during a match. It looks like that:

.. image:: ../_static/images/doublejump1.jpg

You can use it to offer you players another way of moving around in the arena.

**Note:** You might need to adjust the horizontal strength value when your arena is bigger or smaller than the sample arena.

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena
2. Open the **Abilities Page**
3. Open the **DoubleJump Page**

.. image:: ../_static/images/doublejump2.jpg

4. Now continue by enabling or disabling the doubleJump by clicking **toggle** with your mouse.
5. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this page.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**
::
   double-jump:
      enabled: true
      cooldown: 2
      vertical-strength: 1.0
      horizontal-strength: 2.0
      particle-effect:
        effecting: EVERYONE
        name: explosion
        amount: 4
        speed: 2.0
        offset:
          x: 2.0
          y: 2.0
          z: 2.0
      sound-effect:
        effecting: EVERYONE
        name: GHAST_FIREBALL
        volume: 100.0
        pitch: 1.0

3. Now continue by enabling or disabling the doubleJump by changing the **enabled** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Enabled: Enable or disable the doubleJump
* Cooldown: Cooldown in seconds between the time a player activates the jump and can use another jump
* Vertical strength: Strength modifier for the y axe
* Horizontal strength: Strength modifier for the x and z axe
* Particleeffect: `Particleeffect <particleeffect.html>`_  being played when a player clicks space a second time
* Soundeffect: `Soundeffect <soundeffect.html>`_  being played when a player clicks space a second time










