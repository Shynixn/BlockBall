The ball takes very long to move when you touch it?
==================================================

The check how often interaction is detected with the ball is bound to the amount of ticks an armorstand is ticked and the hitbox size of the invisible slime.

Change the hitbox
~~~~~~~~~~~~~~~~~~~~

The hitbox can be customized using BlockBall which is the first thing you should try out.
Open the arena file and change the following values:

**before - arena_x.yml**

.. code-block:: yaml

    ball:
      size: NORMAL
      skin: http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d
      hitbox-size: 3.0
      hitbox-relocation: 0.0

Play around with the **hitbox-size** and **hitbox-relocation** value and try different values. You can see the hitbox
by entering the spectator gamemode **/gamemode spectator** while participating in a game.

.. image:: ../_static/images/slimehitbox.PNG

**after - arena_x.yml**

.. code-block:: yaml

    ball:
      size: NORMAL
      skin: http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d
      hitbox-size: 5.0
      hitbox-relocation: 0.2


Change the server ticks
~~~~~~~~~~~~~~~~~~~~~~~

Open the **spigot.yml** and (if it exists) **paper.yml** files and customize the ticking values of entities (armorstands).