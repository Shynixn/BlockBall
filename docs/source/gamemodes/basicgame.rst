Base Game
=========

The Base Game contains the min amount of configuration to setup a working BlockBall game. It also provides the tutorial
to use the Chat UI.

Getting in touch with the Chat UI
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Unlike the old Chat UI this Chat UI is fully compatible to all chat plugins and passes arguments over commands.

1. Let's start by typing **/blockball**. This UI should show up.

.. image:: ../_static/images/arena1.jpg

If the Chat UI is only partially formatted correctly, you should temporarily adjust your client chat settings.

.. image:: ../_static/images/arena3.jpg

2. The UI is very simple to use, simply move your mouse on top of the colored text like '*[create..]*' or '*>>SAVE<<*'. Even tooltips are visible.

.. image:: ../_static/images/arena4.jpg


Creating the arena
~~~~~~~~~~~~~~~~~~

1. As displayed on the previous screenshot simply click on '*[create..]*' with your mouse.

.. image:: ../_static/images/arena2.jpg

2. You can see that different actions do have different colors.

* The action '*[edit..]* requires text input when being clicked. It suggests the command for changing the displayname of the arena in this case. Simply enter the name, it supports spaces and chat colors!

.. image:: ../_static/images/arena5.jpg

.. image:: ../_static/images/arena6.jpg


* The action *[toggle..]* simply behaves as a switch between true and false.
* The action *[worledit..]* copies your current worldedit selection.
* The action *[location..]* copies your current player position.
* You do not have to remember all of this, all actions provide tooltips by hovering the mouse on top of them.

3. Select the the arena via worldedit and press *[worldedit..]* at Center.

.. image:: ../_static/images/arena7.png

4. Use the same technique to select the goal for the red and blue team.

5. Move your character to the center or ball spawnpoint of the arena and press *[location..]*

6. Last but not least do not forget to always save your changes. If you press *>>Save<<* only the arena properties
get saved and does not have an impact on other BlockBall games running on your server, pressing *>>Save and reload<<**
restarts all BlockBall games on your server and starts initial games.

7. In this case we have to press *>>Save and reload<<* for our game to start.


Checking if everything is working
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Let's check if our base game is correctly configured. Run into the forcefield of your arena and take a look if the
following message appears.

.. image:: ../_static/images/arena9.jpg

If it does not, your worledit arena selection is probably misplaced or simply wrong.

Now you should automatically spawn at the same spawnpoint of the ball. You can customize your team spawnpoint later in the
team section.

Check if the ball is spawning and goals get scored by move the ball into one of your configured goals.

If it does not, your worledit goal selection is probably misplaced or simply wrong.

Putting all together
~~~~~~~~~~~~~~~~~~~~

* You have learned how the Chat UI works
* These steps will always be same when you are creating a new arena regardless of the game mode.
* Let's continue with finally playing the game by using the Game Mode HubGame.
* You can also continue with creating a Minigame or BungeeCord Minigame but for beginners it is recommend to create a HubGame first.







