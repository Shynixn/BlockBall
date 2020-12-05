Ball
====

One of the most outstanding features of BlockBall is it to provide a really awesome looking ball with its own physic engine. You
**do not** have to install a mod or Texture Pack in order to make this possible.

The original physic engine of the Ball was developed back in 2015 which was replaced by a more advanced engine in 2018. Finally,
the engine was completely redesigned from scratch to fully run on packets only in 2020.

.. image:: ../_static/images/ball3.png

You can even set your own skin or custom texture model to it.

Configuring ingame
~~~~~~~~~~~~~~~~~~

The Ball is per default correctly configured but can be adjusted to deal with different situations like small or a very large arenas.

1. Go to the **Settings Page** of your arena
2. Open the **Ball Settings Page**

.. image:: ../_static/images/ball1.JPG

3. Customize the skin or other values to your own needs.
4. Open the Ball modifiers page to adjust even more physic engine modifiers.

.. image:: ../_static/images/ball2.JPG

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this `page <../general/database.html#editing-the-arena-files>`__.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**

.. code-block:: yaml

    ball:
      size: NORMAL
      skin: http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d
      interaction-hitbox-size: 2.0
      kickpass-hitbox-size: 5.0
      kickpass-delay-ticks: 5
      interaction-cooldown-ticks: 20
      rotating: true
      enable-kick: true
      enable-pass: true
      enable-interact: true
      hitbox-relocation: 0.0
      always-bounce: true
      modifiers:
        gravity-mod: 0.07
        air-resistance: 0.001
        rolling-resistance: 0.1
        horizontal-touch: 1.0
        vertical-touch: 1.0
        shot-velocity: 1.5
        pass-velocity: 1.2
        max-spin: 0.08
        max-pitch: 60
        min-pitch: 0
        default-pitch: 20
      particle-effects:
        ONINTERACTION:
          name: NONE
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
          data: 0
        ONMOVE:
          name: NONE
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
          data: 0
        ONSPAWN:
          name: EXPLOSION_NORMAL
          amount: 10
          speed: 0.1
          offset:
            x: 2.0
            y: 2.0
            z: 2.0
          data: 0
        ONKICK:
          name: EXPLOSION_LARGE
          amount: 2
          speed: 0.1
          offset:
            x: 0.1
            y: 0.1
            z: 0.1
          data: 0
        ONGOAL:
          name: NONE
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
          data: 0
        ONTHROW:
          name: NONE
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
          data: 0
        ONGRAB:
          name: NONE
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
          data: 0
      sound-effects:
        ONINTERACTION:
          name: none
          pitch: 0.0
          volume: 0.0
          effecting: EVERYONE
        ONMOVE:
          name: none
          pitch: 0.0
          volume: 0.0
          effecting: EVERYONE
        ONSPAWN:
          name: none
          pitch: 0.0
          volume: 0.0
          effecting: EVERYONE
        ONKICK:
          name: ENTITY_ZOMBIE_ATTACK_DOOR_WOOD
          pitch: 1.0
          volume: 10.0
          effecting: EVERYONE
        ONGOAL:
          name: none
          pitch: 0.0
          volume: 0.0
          effecting: EVERYONE
        ONTHROW:
          name: none
          pitch: 0.0
          volume: 0.0
          effecting: EVERYONE
        ONGRAB:
          name: none
          pitch: 0.0
          volume: 0.0
          effecting: EVERYONE
      spawn-delay: 0
      spawnpoint:
        world: world
        x: 1587.4580324859699
        y: 4.0
        z: -1606.6457980542893
        yaw: 169.0550537109375
        pitch: 28.3505802154541

3. Customize the available options to your own needs.

Properties
~~~~~~~~~~

* Size: Size of the ball. Can be NORMAL or SMALL.
* Skin: Name of a player using this skin or a skin URL.
* Interaction Hitbox Size: Size of the ball hitbox intercepting player movements. Does not have an impact on left or right clicking the ball.
* KickPass Hitbox Size: Size of the ball hitbox intercepting player left clicks and right clicks. Does not have an impact on player movements.
* KickPass Delay: Delay in ticks until the ball executes the movement action after being clicked.
* Interaction Cooldown: Delay in ticks until the ball can detect another movement action.
* Rotating: Should the ball rotate depending on its direction and speed?
* Enable Kick: Should the ball detect left clicks on it?
* Enable Pass: Should the ball detect right clicks on it?
* Enable Interact: Should the ball detect player movements?
* Hitbox Relocation: Y-Axe offset to make the ball move more close to the ground or higher up.
* Always Bounce: Should the ball bounce of from blocks?
* Ball modifiers: Collection of modifiers explained below.
* Spawn Delay: Delay until the ball spawns.
* Spawnpoint: Ball spawnpoint properties.
* Sound-effect: Sound being played for a certain action.
* Particle-effect: Particle being generated for a certain action.

Modifier Properties
~~~~~~~~~~~~~~~~~~~

* Gravity Modifier: Strength of the gravity. A negative value allows the ball to float into inifinity.
* Air Resistance: The reducement of speed per tick if the ball flies in the air.
* Rolling Resistance: The reducement of speed per tick if the ball rolls on the ground.
* Horizontal Touch: The horizontal speed modifier the ball flies when a player runs into the ball.
* Vertical Touch: The vertical speed modifier the ball flies when a player runs into the ball.
* Shoot Velocity: The speed modifier when leftclicking the ball.
* Pass Velocity: The speed modififer when rightclicking the ball.
* Max spin for the magnus force calculation.
* Max pitch for the magnus force calculation.
* Min pitch for the magnus force calculation.
* Default pitch for the magnus force calculation.
