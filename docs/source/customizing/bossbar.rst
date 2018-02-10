Bossbar
=======

It is possible to display a bossbar during a match on a players screen. It looks like that:

.. image:: ../_static/images/bossbar1.jpg

You can use it to display team names, the remaining time, scores for each team and more. You can find all available placeholders here.

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the 'Settings' page of your arena chat UI. If not, please take a look at this page.

1. Go to the **Settings Page** of your arena
2. Open the **Effects Page**
3. Open the **Bossbar Page**

.. image:: ../_static/images/bossbar2.jpg

4. Now continue by enabling or disabling the bossbar by clicking **toggle** with your mouse.
5. Customize the other available options to your own needs.

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this page.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**
::
    bossbar:
      enabled: true
      text: '<redcolor><red> <redscore> : <bluecolor><bluescore> <blue>'
      percentage: 50.0
      color: WHITE
      style: SOLID
      flags:
        '1': DARKEN_SKY


3. Now continue by enabling or disabling the bossbar by changing the **enabled** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Message: Message being displayed as title above the boss health bar. Supports all available placeholders.
* Enabled: Enable or disable the bossbar
* Percentage: Percentage how much the boss health bar is filled (0-100)
* Color: Color of the bossbar
* Style: Different displaying styles of the bossbar
* Flags: Additional flags










