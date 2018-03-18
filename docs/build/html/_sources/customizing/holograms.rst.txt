Holograms
=========

It is possible to display multiple holograms during a match all around of the arena. It looks like that:

.. image:: ../_static/images/hologram1.jpg

You can use it to display team names, the remaining time, scores for each team and more. You can find all available placeholders here.

Holograms do even support multiple lines:

.. image:: ../_static/images/hologram2.jpg

Configuring ingame
~~~~~~~~~~~~~~~~~~

At this stage, you should be able to open the `Settings <../gamemodes/basicgame.html#getting-in-touch-with-the-chat-ui>`__ page of your arena chat UI.

1. Go to the **Settings Page** of your arena
2. Open the **Effects Page**
3. Open the **Holograms Page**

.. image:: ../_static/images/hologram3.jpg

4. Now continue by adding a new hologram by pressing **add by location**
5. Customize the other available options to your own needs.

.. image:: ../_static/images/hologram4.jpg

You can find all options explained at the bottom of this page.

Configuring in your arena_x.yml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At this stage, you should be able to find your arena save file. If not, please take a look at this `page <../general/database.html#editing-the-arena-files>`__.

1. Go to the **Open the arena_x.yml** of your arena
2. Search for the following section:

**arena_x.yml**
::
   holograms:
      '1':
        lines:
        - '<redcolor><red> <redscore> : <bluecolor><bluescore> <blue>'
        location:
          x: 89.47854663146359
          y: 66.0
          z: 195.8284189833682
          yaw: 197.9395294189453
          pitch: 21.89979362487793
          world: world
      '2':
        lines:
        - '<redcolor><red> <redscore> : <bluecolor><bluescore> <blue>'
        location:
          x: 83.53704438102707
          y: 66.0
          z: 183.90150615049936
          yaw: 342.53973388671875
          pitch: 13.34978199005127
          world: world


3. Now continue by using one of the templates.
4. Customize the other available options to your own needs.

Properties
~~~~~~~~~~

* Location: Default location where the hologram is going to appear during a match
* Lines: All lines of text of the hologram.










