Ball
====

One of the most outstanding features of BlockBall is it to provide a really awesome looking ball with working animations. You
**do not** have to install a mod or Texture Pack in order to make this possible.

The origin physic engine of the Ball was developed back in 2015 and is now replaced by the more advanced 2018 engine. There are many
modifier values you can adjust.

.. image:: ../_static/images/ball3.png

You can even set your own skin or custom texture model to it.

Configuring ingame
~~~~~~~~~~~~~~~~~~

The Ball is per default correctly configured but can be changed for different situations like a small or a very large arena.



1. Go to the **Settings Page** of your arena
2. Open the **Ball Settings Page**

.. image:: ../_static/images/ball1.JPG

3. Customize the skin or other values to your own needs.
4. Open the Ball modifiers page to adjust modifiers like the rolling distance

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
      skin: http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d
      size: NORMAL
      hitbox-size: 3.0
      carry-able: false
      always-bounce: true
      rotating: true
      modifiers:
        horizontal-touch: 1.0
        vertical-touch: 1.0
        horizontal-kick: 1.5
        vertical-kick: 1.5
        horizontal-throw: 1.0
        vertical-throw: 1.0
        rolling-distance: 1.0
        gravity: 1.0
      particle-effects:
        onmove:
          effecting: EVERYONE
          name: none
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
        onkick:
          effecting: EVERYONE
          name: none
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
        onspawn:
          effecting: EVERYONE
          name: none
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
        onthrow:
          effecting: EVERYONE
          name: none
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
        oninteraction:
          effecting: EVERYONE
          name: none
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
        ongrab:
          effecting: EVERYONE
          name: none
          amount: 0
          speed: 0.0
          offset:
            x: 0.0
            y: 0.0
            z: 0.0
      sound-effects:
        onmove:
          effecting: EVERYONE
          name: none
          volume: 0.0
          pitch: 0.0
        onkick:
          effecting: EVERYONE
          name: none
          volume: 0.0
          pitch: 0.0
        onspawn:
          effecting: EVERYONE
          name: none
          volume: 0.0
          pitch: 0.0
        onthrow:
          effecting: EVERYONE
          name: none
          volume: 0.0
          pitch: 0.0
        oninteraction:
          effecting: EVERYONE
          name: none
          volume: 0.0
          pitch: 0.0
        ongrab:
          effecting: EVERYONE
          name: none
          volume: 0.0
          pitch: 0.0
      wall-bouncing: {}

3. Customize the available options to your own needs.

Properties
~~~~~~~~~~

* Skin: Name of a player using this skin or a skin URL.
* Size: Size of the ball. Can be NORMAL or SMALL.
* Hitbox Size: Size of the ball hitbox intercepting player movements. Does not have an impact on left or right clicking the ball.
* Carry Able: Should the Ball be able to grabbed by players. Should not be used for BlockBall games yet only for fun and testing.
* Always Bounce: Should the ball bounce on walls?
* Rotation Animation: Should the ball rotate depending on its direction and speed?
* Ball modifiers: Collection of modifiers explained below.
* Sound-effect: Sound being played for a certain action.
* Particle-effect: Particle being generated for a certain action.

Modifier Properties
~~~~~~~~~~~~~~~~~~~

* Touch Strength: Horizontal or Vertical speed/distance modifier the ball flies when an animal, monster or player runs into the ball.
* Kick Strength: Horizontal or Vertical speed/distance modifier the ball flies when a player leftclicks the ball.
* Throw Strength: Horizontal or Vertical speed/distance modifier the ball flies when a player grabs the ball by rightclicking it and clicking again to throw it.
* Rolling Distance: The speed/distance modifier the ball rolls after being touched, kicked or thrown
* Gravity Modifier: The speed modifier how fast a ball falls onto the ground. A negative value allows the ball to float into infinity.










