Soundeffect
===========

It is possible to play so called Soundeffects when a certain action appears. This effect cannot be configured
standalone as it always belongs to another action like DoubleJump.

You can set whether **all players** can hear the Soundeffect, **only the player belonging to the action** for example DoubleJump
or **nobody** for performance reasons.

Configuring ingame
~~~~~~~~~~~~~~~~~~

As the Soundeffect is always a sub page of another action there is no specific page.

1. The Soundeffect page looks like that.

.. image:: ../_static/images/soundeffect1.JPG

4. Now continue by selecting which player should be able to see the Soundeffect by clicking on effecting
5. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

As the Soundeffect is always a sub effect of another action there is no specific section.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following sections:

**arena_x.yml**

.. code-block:: yaml

  sound-effect:
    effecting: EVERYONE
    name: ENTITY_GHAST_SHOOT
    volume: 100.0
    pitch: 1.0

3. Now continue by changing the **effecting** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Effecting: Whether all players can hear the Soundeffect, only the player belonging to the action or nobody.
* Name: Name of the Soundeffect. All names can be found `here. <https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html>`_
* Volume: Volume of the Soundeffect (radius of blocks the sound is audible)
* Pitch: Pitch of the Soundeffect











