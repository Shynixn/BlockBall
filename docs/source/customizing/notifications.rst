Notifications
=============

The notification settings allow to setup messages for nearby players.

This allows players standing near the arena to get to know what is going on inside of the arena as they also
get the same **messages**, **scoreboard**, **bossbar** and **holograms** of the player playing the game.

.. image:: ../_static/images/notifications_1.JPG

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena
2. Open the **Spectating Page**
3. Open the **Notifications Page**

.. image:: ../_static/images/notifications_2.JPG

4. Now continue by enabling or disabling the notifications by clicking **toggle** with your mouse.
5. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this `page <../general/database.html#editing-the-arena-files>`__.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**

.. code-block:: yaml

   spectator-meta:
      notify-nearby-players-enabled: true
      notify-nearby-players-radius: 100

3. Now continue by enabling or disabling the notifications by changing the **notify-nearby-players-enabled** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Notify-nearby-players-enabled: Enable or disable notifications for nearby players.
* Notify-nearby-players-radius: The amount of blocks a player has to be near an arena to still receive notifications.










