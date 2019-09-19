Particleeffect
==============

It is possible to show so called Particleeffects when a certain action appears. This effect cannot be configured
standalone as it always belongs to another action like DoubleJump.

You can set whether **all players** can see the Particleeffect, **only the player belonging to the action** for example DoubleJump
or **nobody** for performance reasons.

Configuring ingame
~~~~~~~~~~~~~~~~~~

As the Particleeffect is always a sub page of another action there is no specific page.

1. The Particleeffect page looks like that.

.. image:: ../_static/images/particleeffect1.JPG

4. Now continue by selecting which player should be able to see the Particleeffect by clicking on effecting
5. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

As the Particleeffect is always a sub effect of another action there is no specific section.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following sections:

**arena_x.yml**

.. code-block:: yaml

     particle-effect:
        effecting: EVERYONE
        name: explosion
        amount: 4
        speed: 2.0
        offset:
          x: 2.0
          y: 2.0
          z: 2.0

3. Now continue by changing the **effecting** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Effecting: Whether all players can see the Particleeffect, only the player belonging to the action or nobody.
* Name: Name of the Particleeffect. All names can be found `here. <https://shynixn.github.io/BlockBall/apidocs/com/github/shynixn/blockball/api/business/enumeration/ParticleType.html>`_
* Amount: Amount of particles in the air
* Speed: Speed of each particle in the air
* Offset X: Offset spread of the x axe
* Offset Y: Offset spread of the y axe
* Offset Z: Offset spread of the z axe











