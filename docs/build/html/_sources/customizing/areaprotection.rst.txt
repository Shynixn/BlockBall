AreaProtection
==============

BlockBall does not offer a block or region protection like Worldguard but it contains a system to protect entities like
players, animals and monsters to enter the arena.

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena
2. Open the **Misc Page**
3. Open the **Arena Protection Page**

.. image:: ../_static/images/customizing1.JPG

4. Now continue by enabling or disabling the protections.
5. Customize the other available options to your own needs.

It is recommend to leave these settings on default. Do only change the velocity values if your server cannot not adjust
to the configured default speed or entities should fly to a specific location.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this `page <../general/database.html#editing-the-arena-files>`__.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**

.. code-block:: yaml

   protection:
      entity-protection-enabled: true
      entity-protection:
        x: 5.0
        y: 2.0
        z: 5.0
      rejoin-protection-enabled: true
      rejoin-protection:
        x: 0.0
        y: 2.0
        z: 0.0

3. Now continue by enabling or disabling the protections by changing the **enabled** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Animal and Monster protection enabled: Should animals and monster be thrown out of the arena with the given velocity when they try to enter it?
* Animal and Monster protection velocity: Velocity being applied when they enter the arena.
* Join protection enabled: Should player be the certain velocity being applied when they get stuck in the forcefield?
* Join protection velocity: Velocity being applied when they get stuck in the forcefield.










