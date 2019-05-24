The ball falls through the ground?
==================================

Sometimes the ball is not synchronized with your world or simply requires another relocation distance
for your server version.

1. Make sure the thickness of the ground is at least **2 or more blocks**
2. If the ball does still fall through the ground you can try to modify the hidden **hitbox-relocation** value of the ball.

**before - arena_x.yml**

.. code-block:: yaml

   ball:
      skin: http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d
      size: SMALL
      hitbox-size: 3.0
      hitbox-relocation: 0.0
      carry-able: true
      always-bounce: true
      rotating: true

Test it with different values like 0.2, 0.5 and take a look what fits best with your server.

**after - arena_x.yml**

.. code-block:: yaml

   ball:
      skin: http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d
      size: SMALL
      hitbox-size: 3.0
      hitbox-relocation: 0.2
      carry-able: true
      always-bounce: true
      rotating: true







