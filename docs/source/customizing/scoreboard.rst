Scoreboard
==========

It is possible to display a scoreboard during a match on a players screen. It looks like that:

.. image:: ../_static/images/scoreboard1.jpg

You can use it to display team names, the remaining time, scores for each team and more. You can find all available placeholders here.

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena
2. Open the **Effects Page**
3. Open the **Scoreboard Page**

.. image:: ../_static/images/scoreboard2.jpg

4. Now continue by enabling or disabling the scoreboard by clicking **toggle** with your mouse.
5. Customize the other available options to your own needs.

.. image:: ../_static/images/scoreboard3.jpg


You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this page.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**
::
    scoreboard:
      title: '&aBlockBall'
      enabled: true
      lines:
      - ''
      - '&6Time: '
      - <time>
      - ''
      - '&m           &r'
      - '<redcolor><red>:'
      - '&l<redscore>'
      - ''
      - '<bluecolor><blue>:'
      - '&l<bluescore>'
      - '&m           &r'


3. Now continue by enabling or disabling the scoreboard by changing the **enabled** value.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Title: Title of the scoreboard. Supports all available placeholders.
* Enabled: Enable or disable the scoreboard
* Lines: You can add or remove lines and all lines support chatcolors and placeholders.










